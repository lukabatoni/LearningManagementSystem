package com.example.lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        System.currentTimeMillis()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }
}
