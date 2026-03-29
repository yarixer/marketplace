package com.yarixer.marketplace.admin.api;

public record AdminRevisionImageResponse(
        Long imageId,
        String originalFilename,
        String mimeType,
        long sizeBytes,
        int sortOrder,
        boolean cover,
        String url
) {
}