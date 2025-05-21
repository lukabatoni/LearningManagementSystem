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
  private final String fromEmail = "hello@example.com";
  private final String fromName = "Mailtrap Test";

  public MailtrapEmailService() {
    MailtrapConfig config = new MailtrapConfig.Builder()
        .sandbox(true)
        .inboxId(3713999L)
        .token("bd8414f579199eda58f71a2eb166114a")
        .build();
    this.client = MailtrapClientFactory.createMailtrapClient(config);
  }

  public void send(String to, String subject, String text) {
    MailtrapMail mail = MailtrapMail.builder()
        .from(new Address(fromEmail, fromName))
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
