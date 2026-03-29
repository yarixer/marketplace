package com.yarixer.marketplace.auth.api;

import java.util.List;

public record AuthUserResponse(
        Long id,
        String email,
        String displayName,
        List<String> roles,
        boolean seller,
        SellerProfileResponse sellerProfile
) {
}