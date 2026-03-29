package com.yarixer.marketplace.admin.api;

public record AdminProductListItemResponse(
        Long productId,
        String slug,
        String sellerPublicName,
        String state,
        String workflowStatus,
        String title
) {
}