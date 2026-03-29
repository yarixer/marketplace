package com.yarixer.marketplace.seller.api;

import com.yarixer.marketplace.auth.security.AppUserPrincipal;
import com.yarixer.marketplace.seller.service.SellerStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerStatsController {

    private final SellerStatsService sellerStatsService;

    @GetMapping("/monthly-sales")
    public List<SellerMonthlySalesPointResponse> getMonthlySales(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return sellerStatsService.getMonthlySales(principal.getId());
    }

    @GetMapping("/products")
    public List<SellerProductStatsRowResponse> getProductStats(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return sellerStatsService.getProductStats(principal.getId());
    }
}