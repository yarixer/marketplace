package com.yarixer.marketplace.auth.repository;

import com.yarixer.marketplace.auth.domain.AuthRefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long> {
    Optional<AuthRefreshToken> findByTokenHash(String tokenHash);
}