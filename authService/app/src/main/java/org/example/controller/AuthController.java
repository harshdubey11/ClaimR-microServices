package org.example.controller;

import java.util.UUID;

import org.example.dto.BaseResponse;
import org.example.dto.LoginRequestDTO;
import org.example.dto.LoginResponseDTO;
import org.example.dto.RegisterRequestDTO;
import org.example.dto.UserDetailsResponseDTO;
import org.example.dto.ValidateTokenRequestDTO;
import org.example.dto.ValidateTokenResponseDTO;
import org.example.entity.User;
import org.example.service.AuthenticationService;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

        private final AuthenticationService authService;

        public AuthController(AuthenticationService authService) {
                this.authService = authService;
        }

        @PostMapping("/register")
        public BaseResponse<String> register(
                        @RequestBody RegisterRequestDTO request) {

                User user = authService.register(request);

                return new BaseResponse<>(
                                "User registered successfully",
                                "SUCCESS");
        }

        @PostMapping("/login")
        public BaseResponse<LoginResponseDTO> login(
                        @RequestBody LoginRequestDTO request) {

                LoginResponseDTO response = authService.login(request);

                return new BaseResponse<>(
                                response,
                                "Login successful");
        }

        @PostMapping("/validate")
        public BaseResponse<ValidateTokenResponseDTO> validate(
                        @RequestBody ValidateTokenRequestDTO request) {

                ValidateTokenResponseDTO response = authService.validateToken(
                                request.getToken());

                return new BaseResponse<>(
                                response,
                                "Token validation completed");
        }

        @GetMapping("/users/{id}")
        public BaseResponse<UserDetailsResponseDTO> getUserById(
                        @PathVariable @NonNull UUID id) {

                User user = authService.getUserById(id);

                return new BaseResponse<>(
                                new UserDetailsResponseDTO(
                                                user.getId(),
                                                user.getEmail()),
                                "User details fetched successfully.");
        }
}
