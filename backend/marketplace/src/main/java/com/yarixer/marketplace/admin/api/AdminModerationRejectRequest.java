package com.yarixer.marketplace.admin.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminModerationRejectRequest(
        @NotBlank
        @Size(max = 2000)
        String rejectionReason
) {
}