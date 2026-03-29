package com.yarixer.marketplace.support;

import com.yarixer.marketplace.storage.StorageService;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestStorageConfig {

    @Bean
    @Primary
    public StorageService storageService() {
        return new InMemoryStorageService();
    }

    static class InMemoryStorageService implements StorageService {

        private final Set<String> objectKeys = ConcurrentHashMap.newKeySet();

        @Override
        public void putObject(String objectKey, InputStream inputStream, long size, String contentType) {
            objectKeys.add(objectKey);
        }

        @Override
        public void deleteObject(String objectKey) {
            objectKeys.remove(objectKey);
        }

        @Override
        public String createPresignedGetUrl(String objectKey, Duration ttl) {
            if (!objectKeys.contains(objectKey)) {
                throw new IllegalStateException("Object does not exist in test storage: " + objectKey);
            }

            return "https://test-storage.local/object/"
                    + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
        }
    }
}