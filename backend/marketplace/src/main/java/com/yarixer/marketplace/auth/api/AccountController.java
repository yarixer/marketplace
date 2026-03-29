package com.yarixer.marketplace.auth.api;

import com.yarixer.marketplace.auth.security.AppUserPrincipal;
import com.yarixer.marketplace.auth.service.AuthService;
import com.yarixer.marketplace.wallet.api.WalletBalanceResponse;
import com.yarixer.marketplace.wallet.service.WalletLedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthService authService;
    private final WalletLedgerService walletLedgerService;

    @GetMapping("/me")
    public AuthUserResponse me(@AuthenticationPrincipal AppUserPrincipal principal) {
        return authService.getCurrentUser(principal.getId());
    }

    @PatchMapping("/profile")
    public AuthUserResponse updateProfile(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return authService.updateProfile(principal.getId(), request);
    }

    @PostMapping("/change-password")
    public void changePassword(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(principal.getId(), request);
    }

    @GetMapping("/wallet")
    public WalletBalanceResponse wallet(@AuthenticationPrincipal AppUserPrincipal principal) {
        return walletLedgerService.getCurrentWallet(principal.getId());
    }

    @PostMapping("/become-seller")
    public AuthUserResponse becomeSeller(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody BecomeSellerRequest request
    ) {
        return authService.becomeSeller(principal.getId(), request);
    }
}