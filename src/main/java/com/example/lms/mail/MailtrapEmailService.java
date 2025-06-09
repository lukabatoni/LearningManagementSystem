package com.example.lms.mail;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class MailtrapEmailService implements EmailService {

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

  @Override
  public void sendEmail(EmailDetails details) {
    String[] recipients = details.getRecipient();
    if (recipients == null) {
      return;
    }
    for (String to : recipients) {
      if (to == null || to.trim().isEmpty()) {
        continue;
      }
      MailtrapMail mail = MailtrapMail.builder()
          .from(new Address(properties.getFromMail(), properties.getFromName()))
          .to(List.of(new Address(to)))
          .subject(details.getSubject())
          .text(details.getMsgBody())
          .category("Notification")
          .build();
      try {
        client.send(mail);
      } catch (Exception e) {
        throw new RuntimeException("Failed to send email", e);
      }
    }
  }
}
