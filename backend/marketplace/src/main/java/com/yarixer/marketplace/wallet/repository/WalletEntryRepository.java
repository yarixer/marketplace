package com.yarixer.marketplace.wallet.repository;

import com.yarixer.marketplace.wallet.domain.WalletEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletEntryRepository extends JpaRepository<WalletEntry, Long> {
    List<WalletEntry> findAllByWalletAccount_IdOrderByCreatedAtDesc(Long walletAccountId);
}