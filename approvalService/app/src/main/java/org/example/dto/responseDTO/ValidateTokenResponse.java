package org.example.dto.responseDTO;

import java.util.UUID;

public class ValidateTokenResponse {

    private boolean valid;

    private String email;

    private String role;

    private UUID userId;

    public ValidateTokenResponse() {
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UUID getUserId(){
        return this.userId;
    }

    public void setUserId(UUID userId){
        this.userId = userId;
    }
}