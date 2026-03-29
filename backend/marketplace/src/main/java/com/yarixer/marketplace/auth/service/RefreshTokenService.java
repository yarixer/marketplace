package com.yarixer.marketplace.auth.service;

import com.yarixer.marketplace.auth.config.AuthProperties;
import com.yarixer.marketplace.auth.domain.AuthRefreshToken;
import com.yarixer.marketplace.auth.repository.AuthRefreshTokenRepository;
import com.yarixer.marketplace.user.domain.AppUser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final AuthRefreshTokenRepository authRefreshTokenRepository;
    private final AuthProperties authProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public IssuedRefreshToken issueToken(AppUser user, String clientIp, String userAgent) {
        String rawToken = generateRawToken();
        String tokenHash = sha256(rawToken);

        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC)
                .plus(authProperties.getRefreshTokenTtl());

        AuthRefreshToken refreshToken = new AuthRefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(expiresAt.toLocalDateTime());
        refreshToken.setCreatedByIp(truncate(clientIp, 64));
        refreshToken.setUserAgent(truncate(userAgent, 500));

        authRefreshTokenRepository.save(refreshToken);

        return new IssuedRefreshToken(rawToken, expiresAt);
    }

    @Transactional
    public RefreshRotationResult rotate(String rawRefreshToken, String clientIp, String userAgent) {
        String existingHash = sha256(rawRefreshToken);
        AuthRefreshToken existing = authRefreshTokenRepository.findByTokenHash(existingHash)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);

        if (existing.isRevoked()) {
            throw new BadCredentialsException("Refresh token is revoked");
        }

        if (existing.isExpired(nowUtc)) {
            throw new BadCredentialsException("Refresh token is expired");
        }

        IssuedRefreshToken newRefreshToken = issueToken(existing.getUser(), clientIp, userAgent);

        existing.setRevokedAt(nowUtc);
        existing.setReplacedByTokenHash(sha256(newRefreshToken.token()));
        authRefreshTokenRepository.save(existing);

        return new RefreshRotationResult(existing.getUser(), newRefreshToken);
    }

    @Transactional
    public void revokeIfPresent(String rawRefreshToken) {
        String tokenHash = sha256(rawRefreshToken);

        authRefreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            if (!token.isRevoked()) {
                token.setRevokedAt(LocalDateTime.now(ZoneOffset.UTC));
                authRefreshTokenRepository.save(token);
            }
        });
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash refresh token", ex);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}