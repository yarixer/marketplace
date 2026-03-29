package com.yarixer.marketplace.auth.domain;

import com.yarixer.marketplace.common.model.AuditableEntity;
import com.yarixer.marketplace.user.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "auth_refresh_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "uk_auth_refresh_tokens_token_hash", columnNames = "token_hash")
})
public class AuthRefreshToken extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "replaced_by_token_hash", length = 64)
    private String replacedByTokenHash;

    @Column(name = "created_by_ip", length = 64)
    private String createdByIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    public boolean isExpired(LocalDateTime nowUtc) {
        return expiresAt.isBefore(nowUtc);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}