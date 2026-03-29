package com.yarixer.marketplace.seller.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerProductCreateRequest(
        @NotBlank
        @Size(max = 160)
        String title
) {
}