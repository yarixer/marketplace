package com.yarixer.marketplace.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LogoutRequest(
        @NotBlank
        @Size(max = 500)
        String refreshToken
) {
}