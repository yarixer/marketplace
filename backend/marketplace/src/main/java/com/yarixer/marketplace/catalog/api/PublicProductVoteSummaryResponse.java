package com.yarixer.marketplace.catalog.api;

public record PublicProductVoteSummaryResponse(
        long positiveCount,
        long negativeCount,
        long score
) {
}