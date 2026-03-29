package com.yarixer.marketplace.buyer.api;

import java.time.LocalDateTime;

public record BuyerPurchaseHistoryRowResponse(
        Long orderId,
        String orderNumber,
        String productName,
        String productSlug,
        LocalDateTime date,
        String status
) {
}