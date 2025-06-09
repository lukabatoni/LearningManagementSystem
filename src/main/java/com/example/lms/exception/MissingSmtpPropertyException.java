package com.example.lms.exception;

public class MissingSmtpPropertyException extends RuntimeException {
  public MissingSmtpPropertyException(String property) {
    super("Missing SMTP property: " + property);
  }
}
