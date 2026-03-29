package com.yarixer.marketplace.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BecomeSellerRequest(
        @NotBlank
        @Size(max = 120)
        String publicName
) {
}