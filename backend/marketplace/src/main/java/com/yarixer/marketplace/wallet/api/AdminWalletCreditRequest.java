package com.yarixer.marketplace.wallet.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminWalletCreditRequest(
        @NotBlank
        @Email
        @Size(max = 255)
        String userEmail,

        @Min(value = 1, message = "amountMinor must be >= 1")
        long amountMinor,

        @NotBlank
        @Size(max = 500)
        String note
) {
}