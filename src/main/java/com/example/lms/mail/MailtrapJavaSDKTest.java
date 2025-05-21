package com.example.lms.mail;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import java.util.List;

public class MailtrapJavaSDKTest {
  private static final String TOKEN = "bd8414f579199eda58f71a2eb166114a";

  public static void main(String[] args) {
    final MailtrapConfig config = new MailtrapConfig.Builder()
        .sandbox(true)
        .inboxId(3713999L)
        .token(TOKEN)
        .build();

    final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

    final MailtrapMail mail = MailtrapMail.builder()
        .from(new Address("hello@example.com", "Mailtrap Test"))
        .to(List.of(new Address("lukaa.oniani2002@gmail.com")))
        .subject("You are awesome!")
        .text("Congrats for sending test email with Mailtrap!")
        .category("Integration Test")
        .build();

    try {
      System.out.println(client.send(mail));
    } catch (Exception e) {
      System.out.println("Caught exception : " + e);
    }
  }
}
