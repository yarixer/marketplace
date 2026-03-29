package com.yarixer.marketplace.buyer.api;

import java.time.LocalDateTime;
import java.util.List;

public record BuyerOrderResponse(
        Long orderId,
        String status,
        long subtotalMinor,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        List<BuyerOrderItemResponse> items
) {
}