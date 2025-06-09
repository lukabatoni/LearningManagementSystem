package com.example.lms.exception;

public class FeatureFlagEvaluationException extends RuntimeException {
  public FeatureFlagEvaluationException(String message, Throwable cause) {
    super(message, cause);
  }
}
