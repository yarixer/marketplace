package com.yarixer.marketplace.wallet.api;

public record WalletBalanceResponse(
        Long walletAccountId,
        String currency,
        long availableMinor,
        long pendingMinor
) {
}