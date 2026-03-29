package com.yarixer.marketplace.admin.api;

import com.yarixer.marketplace.admin.service.AdminUserManagementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;

    @GetMapping
    public List<AdminUserListItemResponse> listUsers() {
        return adminUserManagementService.listUsers();
    }

    @GetMapping("/{userId}")
    public AdminUserDetailsResponse getUser(@PathVariable Long userId) {
        return adminUserManagementService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public AdminUserDetailsResponse updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserUpdateRequest request
    ) {
        return adminUserManagementService.updateUser(userId, request);
    }
}