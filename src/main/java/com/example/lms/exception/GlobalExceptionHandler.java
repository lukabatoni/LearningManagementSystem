package com.example.lms.exception;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final String INVALID_VALUE = "Invalid value";
  private static final String INVALID_REQUEST_DATA = "Invalid request data";
  private static final String VALIDATION_FAILED = "Validation failed";

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        System.currentTimeMillis(),
        Map.of()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> fieldError.getDefaultMessage() != null
                ? fieldError.getDefaultMessage()
                : INVALID_VALUE,
            (msg1, msg2) -> msg1 + "; " + msg2 // merge messages for same field
        ));

    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        VALIDATION_FAILED,
        System.currentTimeMillis(),
        errors
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
    Map<String, String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> fieldError.getDefaultMessage() != null
                ? fieldError.getDefaultMessage()
                : INVALID_VALUE,
            (msg1, msg2) -> msg1 + "; " + msg2 // merge messages for same field
        ));

    return ResponseEntity.badRequest()
        .body(new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            INVALID_REQUEST_DATA,
            System.currentTimeMillis(),
            errors
        ));
  }

  @ExceptionHandler(CoinsNotEnoughException.class)
  public ResponseEntity<ErrorResponse> handleCoinsNotEnoughException(CoinsNotEnoughException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        System.currentTimeMillis(),
        Map.of()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingSmtpPropertyException.class)
  public ResponseEntity<ErrorResponse> handleMissingSmtpHostException(MissingSmtpPropertyException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        ex.getMessage(),
        System.currentTimeMillis(),
        Map.of()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
