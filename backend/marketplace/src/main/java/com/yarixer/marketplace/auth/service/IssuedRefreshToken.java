package com.yarixer.marketplace.auth.service;

import java.time.OffsetDateTime;

public record IssuedRefreshToken(
        String token,
        OffsetDateTime expiresAt
) {
}