package com.yarixer.marketplace.seller.api;

import com.yarixer.marketplace.auth.security.AppUserPrincipal;
import com.yarixer.marketplace.seller.service.SellerProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {

    private final SellerProductService sellerProductService;

    @PostMapping
    public SellerProductDetailsResponse createProduct(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody SellerProductCreateRequest request
    ) {
        return sellerProductService.createProduct(principal.getId(), request);
    }

    @GetMapping
    public List<SellerProductListItemResponse> listProducts(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return sellerProductService.listProducts(principal.getId());
    }

    @GetMapping("/{productId}")
    public SellerProductDetailsResponse getProduct(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long productId
    ) {
        return sellerProductService.getProductDetails(principal.getId(), productId);
    }

    @PutMapping("/{productId}/draft")
    public SellerProductDetailsResponse updateDraft(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long productId,
            @Valid @RequestBody SellerProductDraftUpdateRequest request
    ) {
        return sellerProductService.updateDraft(principal.getId(), productId, request);
    }

    @PostMapping("/{productId}/draft/archive")
    public SellerProductDetailsResponse uploadArchive(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return sellerProductService.uploadArchive(principal.getId(), productId, file);
    }

    @PostMapping("/{productId}/draft/images")
    public SellerProductDetailsResponse uploadImages(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return sellerProductService.uploadImages(principal.getId(), productId, files);
    }

    @DeleteMapping("/{productId}/draft/images/{imageId}")
    public SellerProductDetailsResponse deleteDraftImage(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        return sellerProductService.deleteDraftImage(principal.getId(), productId, imageId);
    }

    @PostMapping("/{productId}/submit-review")
    public SellerProductSubmitReviewResponse submitReview(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long productId
    ) {
        return sellerProductService.submitReview(principal.getId(), productId);
    }
}