package com.example.lms.exception;

public class CoinsNotEnoughException extends RuntimeException {
  public CoinsNotEnoughException(String message) {
    super(message);
  }
}
