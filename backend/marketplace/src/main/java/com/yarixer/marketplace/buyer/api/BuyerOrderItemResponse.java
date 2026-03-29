package com.yarixer.marketplace.buyer.api;

public record BuyerOrderItemResponse(
        Long productId,
        String productSlug,
        Long revisionId,
        String title,
        long priceMinor,
        String currency,
        String sellerPublicName
) {
}