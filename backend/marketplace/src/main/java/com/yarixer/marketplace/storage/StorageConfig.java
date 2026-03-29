package com.yarixer.marketplace.storage;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

    @Bean
    @Profile("!test")
    public MinioClient minioClient(StorageProperties storageProperties) {
        return MinioClient.builder()
                .endpoint(storageProperties.getEndpoint())
                .credentials(storageProperties.getAccessKey(), storageProperties.getSecretKey())
                .build();
    }

    @Bean
    @Profile("!test")
    public StorageService storageService(MinioClient minioClient, StorageProperties storageProperties) {
        return new MinioStorageService(minioClient, storageProperties);
    }
}