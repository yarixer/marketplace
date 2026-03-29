package com.yarixer.marketplace.auth.service;

import com.yarixer.marketplace.user.domain.AppUser;

public record RefreshRotationResult(
        AppUser user,
        IssuedRefreshToken refreshToken
) {
}