package com.yarixer.marketplace.admin.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminProductStateUpdateRequest(
        @NotBlank
        @Pattern(regexp = "^(ACTIVE|ARCHIVED)$")
        String state
) {
}