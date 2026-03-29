package com.yarixer.marketplace.auth.api;

import com.yarixer.marketplace.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthTokensResponse register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent
    ) {
        return authService.register(
                request,
                httpServletRequest.getRemoteAddr(),
                userAgent
        );
    }

    @PostMapping("/login")
    public AuthTokensResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent
    ) {
        return authService.login(
                request,
                httpServletRequest.getRemoteAddr(),
                userAgent
        );
    }

    @PostMapping("/refresh")
    public AuthTokensResponse refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpServletRequest,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent
    ) {
        return authService.refresh(
                request,
                httpServletRequest.getRemoteAddr(),
                userAgent
        );
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
    }
}