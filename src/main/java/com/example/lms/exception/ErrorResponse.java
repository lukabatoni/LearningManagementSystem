package com.example.lms.exception;

import java.util.Map;

public record ErrorResponse(
    int statusCode,
    String message,
    long timestamp,
    Map<String, String> errors
) {
}
