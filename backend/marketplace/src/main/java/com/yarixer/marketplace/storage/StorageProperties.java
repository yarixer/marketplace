package com.yarixer.marketplace.storage;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private Duration downloadUrlTtl;
    private Duration imageUrlTtl;
}