package com.yarixer.marketplace.seller.api;

public record SellerMonthlySalesPointResponse(
        String month,
        long salesCount,
        long grossRevenueMinor
) {
}