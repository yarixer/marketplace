package com.yarixer.marketplace.auth.security;

import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.domain.RoleType;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AppUserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String displayName;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<RoleType> roles;
    private final List<GrantedAuthority> authorities;

    public AppUserPrincipal(
            Long id,
            String email,
            String displayName,
            String passwordHash,
            boolean enabled,
            Set<RoleType> roles
    ) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.roles = roles;
        this.authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    public static AppUserPrincipal from(AppUser user) {
        return new AppUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPasswordHash(),
                user.isEnabled(),
                Set.copyOf(user.getRoles())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}