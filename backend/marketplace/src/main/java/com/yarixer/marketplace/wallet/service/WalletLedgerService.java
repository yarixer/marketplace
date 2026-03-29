package com.yarixer.marketplace.wallet.service;

import com.yarixer.marketplace.common.exception.ResourceNotFoundException;
import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.repository.UserRepository;
import com.yarixer.marketplace.wallet.api.AdminWalletCreditResponse;
import com.yarixer.marketplace.wallet.api.WalletBalanceResponse;
import com.yarixer.marketplace.wallet.domain.WalletAccount;
import com.yarixer.marketplace.wallet.domain.WalletEntry;
import com.yarixer.marketplace.wallet.domain.WalletEntryType;
import com.yarixer.marketplace.wallet.repository.WalletAccountRepository;
import com.yarixer.marketplace.wallet.repository.WalletEntryRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletLedgerService {

    private final WalletAccountRepository walletAccountRepository;
    private final WalletEntryRepository walletEntryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public WalletBalanceResponse getCurrentWallet(Long userId) {
        return walletAccountRepository.findByUser_Id(userId)
                .map(this::toBalance)
                .orElse(new WalletBalanceResponse(null, "USD", 0L, 0L));
    }

    public void creditSaleToSeller(
            Long sellerUserId,
            long amountMinor,
            String referenceType,
            Long referenceId,
            String note
    ) {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("amountMinor must be > 0");
        }

        WalletAccount walletAccount = getOrCreateWallet(sellerUserId);
        walletAccount.setAvailableMinor(walletAccount.getAvailableMinor() + amountMinor);
        walletAccountRepository.save(walletAccount);

        WalletEntry entry = new WalletEntry();
        entry.setWalletAccount(walletAccount);
        entry.setEntryType(WalletEntryType.SALE_CREDIT);
        entry.setAmountMinor(amountMinor);
        entry.setBalanceAfterMinor(walletAccount.getAvailableMinor());
        entry.setReferenceType(referenceType);
        entry.setReferenceId(referenceId);
        entry.setNote(note);
        walletEntryRepository.save(entry);
    }

    public AdminWalletCreditResponse adminCreditUserByEmail(
            String userEmail,
            long amountMinor,
            String note
    ) {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("amountMinor must be > 0");
        }

        String normalizedEmail = userEmail.trim().toLowerCase(Locale.ROOT);

        AppUser user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        WalletAccount walletAccount = getOrCreateWallet(user.getId());
        walletAccount.setAvailableMinor(walletAccount.getAvailableMinor() + amountMinor);
        walletAccountRepository.save(walletAccount);

        WalletEntry entry = new WalletEntry();
        entry.setWalletAccount(walletAccount);
        entry.setEntryType(WalletEntryType.ADMIN_CREDIT);
        entry.setAmountMinor(amountMinor);
        entry.setBalanceAfterMinor(walletAccount.getAvailableMinor());
        entry.setReferenceType("ADMIN_CREDIT");
        entry.setReferenceId(user.getId());
        entry.setNote(note.trim());
        walletEntryRepository.save(entry);

        return new AdminWalletCreditResponse(
                user.getId(),
                user.getEmail(),
                amountMinor,
                walletAccount.getAvailableMinor(),
                walletAccount.getCurrency()
        );
    }

    private WalletAccount getOrCreateWallet(Long userId) {
        return walletAccountRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    AppUser user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                    WalletAccount walletAccount = new WalletAccount();
                    walletAccount.setUser(user);
                    walletAccount.setCurrency("USD");
                    walletAccount.setAvailableMinor(0L);
                    walletAccount.setPendingMinor(0L);
                    return walletAccountRepository.save(walletAccount);
                });
    }

    private WalletBalanceResponse toBalance(WalletAccount walletAccount) {
        return new WalletBalanceResponse(
                walletAccount.getId(),
                walletAccount.getCurrency(),
                walletAccount.getAvailableMinor(),
                walletAccount.getPendingMinor()
        );
    }
}