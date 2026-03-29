package com.yarixer.marketplace.seller.api;

import com.yarixer.marketplace.auth.security.AppUserPrincipal;
import com.yarixer.marketplace.seller.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerDashboardController {

    private final SellerDashboardService sellerDashboardService;

    @GetMapping("/api/seller/dashboard")
    public SellerDashboardResponse getDashboard(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return sellerDashboardService.getDashboard(principal.getId());
    }
}