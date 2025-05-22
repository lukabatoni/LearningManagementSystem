package com.example.lms.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mailtrap")
@Getter
@Setter
public class MailtrapProperties {
  private Long inboxId;
  private String token;
  private boolean sandbox = true;
  private String fromMail;
  private String fromName;
}
