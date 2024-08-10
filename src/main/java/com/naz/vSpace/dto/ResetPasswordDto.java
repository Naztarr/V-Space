package com.naz.vSpace.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDto(
        @NotBlank(message = "Password address is required")
        String newPassword,
        @NotBlank(message = "Confirm your new password")
        String confirmPassword) {
}
