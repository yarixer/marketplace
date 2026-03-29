package com.yarixer.marketplace.admin.api;

import jakarta.validation.constraints.Size;
import java.util.List;

public record AdminUserUpdateRequest(
        @Size(max = 120)
        String displayName,
        Boolean enabled,
        List<String> roles
) {
}