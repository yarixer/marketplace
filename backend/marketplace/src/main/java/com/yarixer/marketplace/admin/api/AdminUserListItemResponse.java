package com.yarixer.marketplace.admin.api;

import java.util.List;

public record AdminUserListItemResponse(
        Long userId,
        String email,
        String displayName,
        boolean enabled,
        List<String> roles,
        boolean seller,
        long availableMinor,
        long pendingMinor
) {
}