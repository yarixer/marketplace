package com.yarixer.marketplace.auth.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    private String jwtSecret;
    private Duration accessTokenTtl;
    private Duration refreshTokenTtl;
}