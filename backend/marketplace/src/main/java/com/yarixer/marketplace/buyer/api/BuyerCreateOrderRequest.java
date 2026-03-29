package com.yarixer.marketplace.buyer.api;

import jakarta.validation.constraints.NotNull;

public record BuyerCreateOrderRequest(
        @NotNull
        Long productId
) {
}