package com.yarixer.marketplace.admin.api;

import com.yarixer.marketplace.admin.service.AdminProductManagementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductManagementController {

    private final AdminProductManagementService adminProductManagementService;

    @GetMapping
    public List<AdminProductListItemResponse> listProducts() {
        return adminProductManagementService.listProducts();
    }

    @GetMapping("/{productId}")
    public AdminProductDetailsResponse getProduct(@PathVariable Long productId) {
        return adminProductManagementService.getProduct(productId);
    }

    @PatchMapping("/{productId}/state")
    public AdminProductDetailsResponse updateProductState(
            @PathVariable Long productId,
            @Valid @RequestBody AdminProductStateUpdateRequest request
    ) {
        return adminProductManagementService.updateProductState(productId, request);
    }
}