package com.stayeasy.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email cannot be blank")
        String email, // (Username)

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}