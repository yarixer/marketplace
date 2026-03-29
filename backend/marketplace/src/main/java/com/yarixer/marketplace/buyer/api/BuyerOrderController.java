package com.yarixer.marketplace.buyer.api;

import com.yarixer.marketplace.auth.security.AppUserPrincipal;
import com.yarixer.marketplace.buyer.service.BuyerOrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class BuyerOrderController {

    private final BuyerOrderService buyerOrderService;

    @PostMapping("/orders")
    public BuyerOrderResponse createOrder(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody BuyerCreateOrderRequest request
    ) {
        return buyerOrderService.createOrder(principal.getId(), request);
    }

    @PostMapping("/orders/{orderId}/mock-complete")
    public BuyerOrderResponse mockCompleteOrder(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long orderId
    ) {
        return buyerOrderService.mockCompleteOrder(principal.getId(), orderId);
    }

    @GetMapping("/orders")
    public List<BuyerOrderResponse> listOrders(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return buyerOrderService.listOrders(principal.getId());
    }

    @GetMapping("/library")
    public List<BuyerLibraryItemResponse> getLibrary(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return buyerOrderService.getLibrary(principal.getId());
    }

    @GetMapping("/library/{productId}/download-link")
    public BuyerDownloadLinkResponse createDownloadLink(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long productId
    ) {
        return buyerOrderService.createDownloadLink(principal.getId(), productId);
    }

    @GetMapping("/purchase-history")
    public List<BuyerPurchaseHistoryRowResponse> getPurchaseHistory(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return buyerOrderService.getPurchaseHistory(principal.getId());
    }
}