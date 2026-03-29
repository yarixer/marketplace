package com.yarixer.marketplace.wallet.api;

import com.yarixer.marketplace.wallet.service.WalletLedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/wallet")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminWalletController {

    private final WalletLedgerService walletLedgerService;

    @PostMapping("/credit")
    public AdminWalletCreditResponse creditUser(
            @Valid @RequestBody AdminWalletCreditRequest request
    ) {
        return walletLedgerService.adminCreditUserByEmail(
                request.userEmail(),
                request.amountMinor(),
                request.note()
        );
    }
}