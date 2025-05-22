package com.example.lms.mail;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MailtrapEmailService {

  private final MailtrapClient client;
  private final MailtrapProperties properties;

  public MailtrapEmailService(MailtrapProperties properties) {
    this.properties = properties;
    MailtrapConfig config = new MailtrapConfig.Builder()
        .sandbox(properties.isSandbox())
        .inboxId(properties.getInboxId())
        .token(properties.getToken())
        .build();
    this.client = MailtrapClientFactory.createMailtrapClient(config);
  }

  public void send(String to, String subject, String text) {
    MailtrapMail mail = MailtrapMail.builder()
        .from(new Address(properties.getFromMail(), properties.getFromName()))
        .to(List.of(new Address(to)))
        .subject(subject)
        .text(text)
        .category("Notification")
        .build();
    try {
      client.send(mail);
    } catch (Exception e) {
      throw new RuntimeException("Failed to send email", e);
    }
  }
}
