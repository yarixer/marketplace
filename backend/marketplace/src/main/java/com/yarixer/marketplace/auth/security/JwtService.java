package com.yarixer.marketplace.auth.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import com.yarixer.marketplace.auth.config.AuthProperties;
import com.yarixer.marketplace.auth.service.IssuedAccessToken;
import com.yarixer.marketplace.user.domain.AppUser;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final AuthProperties authProperties;

    @Getter
    private final JwtEncoder jwtEncoder;

    @Getter
    private final JwtDecoder jwtDecoder;

    public JwtService(AuthProperties authProperties) {
        this.authProperties = authProperties;

        byte[] secretBytes = authProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes long");
        }

        SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    public IssuedAccessToken issueAccessToken(AppUser user) {
        OffsetDateTime issuedAt = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiresAt = issuedAt.plus(authProperties.getAccessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuedAt(issuedAt.toInstant())
                .expiresAt(expiresAt.toInstant())
                .claim("email", user.getEmail())
                .claim("displayName", user.getDisplayName())
                .claim("roles", user.getRoles().stream().map(Enum::name).sorted().toList())
                .build();

        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();

        return new IssuedAccessToken(token, expiresAt);
    }

    public Jwt decodeAccessToken(String token) throws JwtException {
        return jwtDecoder.decode(token);
    }

    public Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Jwt jwt) {
        Object roles = jwt.getClaims().get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}