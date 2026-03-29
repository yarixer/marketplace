package com.yarixer.marketplace.seller.api;

public record SellerProductImageResponse(
        Long imageId,
        String url,
        String originalFilename,
        String mimeType,
        int sortOrder,
        boolean cover
) {
}