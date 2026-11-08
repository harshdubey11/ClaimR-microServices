package org.example.client;

import java.util.UUID;

import org.example.dto.BaseResponse;
import org.example.dto.requestDTO.ValidateTokenRequest;
import org.example.dto.responseDTO.UserDetailsResponseDTO;
import org.example.dto.responseDTO.ValidateTokenResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthClient {

        @PostMapping("/auth/validate")
        BaseResponse<ValidateTokenResponse> validate(
                        @RequestBody ValidateTokenRequest request);

        @GetMapping("/auth/users/{id}")
        BaseResponse<UserDetailsResponseDTO> getUserById(
                        @PathVariable UUID id);
}