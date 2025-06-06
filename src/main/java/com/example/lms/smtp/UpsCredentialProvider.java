package com.example.lms.smtp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "smtp-ups")
@Getter
@Setter
public class UpsCredentialProvider implements SmtpCredentialProvider {
  private String host;
  private int port;
  private String username;
  private String password;
}
