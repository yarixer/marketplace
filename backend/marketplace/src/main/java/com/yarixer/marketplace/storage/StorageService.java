package com.yarixer.marketplace.storage;

import java.io.InputStream;
import java.time.Duration;

public interface StorageService {

    void putObject(String objectKey, InputStream inputStream, long size, String contentType);

    void deleteObject(String objectKey);

    String createPresignedGetUrl(String objectKey, Duration ttl);
}