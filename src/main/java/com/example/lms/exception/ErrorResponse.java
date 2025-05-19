package com.example.lms.exception;

public record ErrorResponse(
    int statusCode,
    String message,
    long timestamp
) {
}
