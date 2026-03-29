package com.yarixer.marketplace.admin.api;

import com.yarixer.marketplace.auth.api.SellerProfileResponse;
import com.yarixer.marketplace.wallet.api.WalletBalanceResponse;
import java.util.List;

public record AdminUserDetailsResponse(
        Long userId,
        String email,
        String displayName,
        boolean enabled,
        List<String> roles,
        SellerProfileResponse sellerProfile,
        WalletBalanceResponse wallet
) {
}