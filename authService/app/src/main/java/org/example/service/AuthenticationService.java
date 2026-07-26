package org.example.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.dto.LoginRequestDTO;
import org.example.dto.LoginResponseDTO;
import org.example.dto.RegisterRequestDTO;
import org.example.dto.ValidateTokenRequestDTO;
import org.example.dto.ValidateTokenResponseDTO;
import org.example.entity.User;
import org.example.exception.UserAlreadyExistsException;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();

   
        user.setName(request.getName());
        user.setEmail(request.getEmail());

       //hash the password
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(request.getRole());
        user.setDepartmentId(request.getDepartmentId());
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    
public LoginResponseDTO login(LoginRequestDTO request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() ->
                    new RuntimeException("Invalid email or password")
            );

    boolean matches = passwordEncoder.matches(
            request.getPassword(),
            user.getPasswordHash()
    );

    if (!matches) {
        throw new RuntimeException("Invalid email or password");
    }

    String token = jwtService.generateToken(
            user.getEmail(),
            user.getRole().name(),
            user.getId()
    );

    return new LoginResponseDTO(
            token,
            user.getRole().name(),
            user.getEmail(),
            user.getName()
    );
}

public ValidateTokenResponseDTO validateToken(String token) {

    boolean valid = jwtService.validateToken(token);

    if (!valid) {

        return new ValidateTokenResponseDTO(
                false,
                null,
                null,
                null
        );
    }

    return new ValidateTokenResponseDTO(
            true,
            jwtService.extractEmail(token),
            jwtService.extractRole(token),
            jwtService.extractUserId(token)
    );
}

}
