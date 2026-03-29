package com.yarixer.marketplace.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final StorageProperties storageProperties;

    @PostConstruct
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(storageProperties.getBucket())
                                .build()
                );
            }
        } catch (Exception ex) {
            throw new StorageOperationException("Unable to initialize storage bucket", ex);
        }
    }

    @Override
    public void putObject(String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageOperationException("Unable to upload object to storage", ex);
        }
    }

    @Override
    public void deleteObject(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageOperationException("Unable to delete object from storage", ex);
        }
    }

    @Override
    public String createPresignedGetUrl(String objectKey, Duration ttl) {
        try {
            int seconds = Math.max(1, (int) Math.min(ttl.toSeconds(), 7L * 24 * 60 * 60));

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .method(Method.GET)
                            .expiry(seconds, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageOperationException("Unable to generate presigned download URL", ex);
        }
    }
}