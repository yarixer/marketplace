package com.yarixer.marketplace.admin.api;

import com.yarixer.marketplace.admin.service.AdminModerationService;
import com.yarixer.marketplace.auth.security.AppUserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/moderation/revisions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminModerationController {

    private final AdminModerationService adminModerationService;

    @GetMapping("/pending")
    public List<AdminPendingRevisionListItemResponse> listPendingRevisions() {
        return adminModerationService.listPendingRevisions();
    }

    @GetMapping("/{revisionId}")
    public AdminModerationRevisionDetailsResponse getPendingRevision(
            @PathVariable Long revisionId
    ) {
        return adminModerationService.getPendingRevisionDetails(revisionId);
    }

    @PostMapping("/{revisionId}/approve")
    public AdminModerationDecisionResponse approve(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long revisionId
    ) {
        return adminModerationService.approveRevision(principal.getId(), revisionId);
    }

    @PostMapping("/{revisionId}/reject")
    public AdminModerationDecisionResponse reject(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long revisionId,
            @Valid @RequestBody AdminModerationRejectRequest request
    ) {
        return adminModerationService.rejectRevision(principal.getId(), revisionId, request);
    }
}