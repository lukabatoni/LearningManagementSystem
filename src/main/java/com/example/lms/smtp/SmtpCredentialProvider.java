package com.example.lms.smtp;

public interface SmtpCredentialProvider {
  String getHost();

  int getPort();

  String getUsername();

  String getPassword();
}
