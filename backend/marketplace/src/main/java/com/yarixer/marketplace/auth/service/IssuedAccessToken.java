package com.yarixer.marketplace.auth.service;

import java.time.OffsetDateTime;

public record IssuedAccessToken(
        String token,
        OffsetDateTime expiresAt
) {
}