package com.yarixer.marketplace.auth.service;

import java.time.OffsetDateTime;

public record AuthTokenPair(
        String accessToken,
        OffsetDateTime accessTokenExpiresAt,
        String refreshToken,
        OffsetDateTime refreshTokenExpiresAt
) {
}