package com.yarixer.marketplace.wallet.api;

public record AdminWalletCreditResponse(
        Long targetUserId,
        String targetUserEmail,
        long creditedAmountMinor,
        long newAvailableMinor,
        String currency
) {
}