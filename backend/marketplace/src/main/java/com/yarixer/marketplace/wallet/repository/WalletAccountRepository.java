package com.yarixer.marketplace.wallet.repository;

import com.yarixer.marketplace.wallet.domain.WalletAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {
    Optional<WalletAccount> findByUser_Id(Long userId);
}