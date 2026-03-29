package com.yarixer.marketplace.common.api;

public record FieldValidationError(
        String field,
        String message
) {
}