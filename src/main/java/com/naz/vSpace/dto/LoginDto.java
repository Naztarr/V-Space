package com.naz.vSpace.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @NotBlank(message = "Email address is required")
        String emailAddress,
        @NotBlank(message = "Password is required")
        String Password) {
}
