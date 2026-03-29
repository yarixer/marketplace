package com.yarixer.marketplace.auth.api;

import java.time.OffsetDateTime;

public record AuthTokensResponse(
        String accessToken,
        OffsetDateTime accessTokenExpiresAt,
        String refreshToken,
        OffsetDateTime refreshTokenExpiresAt,
        AuthUserResponse user
) {
}