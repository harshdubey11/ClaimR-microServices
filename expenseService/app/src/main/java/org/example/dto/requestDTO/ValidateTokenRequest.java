package org.example.dto.requestDTO;

public class ValidateTokenRequest {

    private String token;

    public ValidateTokenRequest() {
    }

    public ValidateTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}