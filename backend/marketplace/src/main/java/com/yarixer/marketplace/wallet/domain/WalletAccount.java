package com.yarixer.marketplace.wallet.domain;

import com.yarixer.marketplace.common.model.AuditableEntity;
import com.yarixer.marketplace.user.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wallet_accounts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_wallet_accounts_user_id", columnNames = "user_id")
})
public class WalletAccount extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "available_minor", nullable = false)
    private long availableMinor = 0L;

    @Column(name = "pending_minor", nullable = false)
    private long pendingMinor = 0L;
}