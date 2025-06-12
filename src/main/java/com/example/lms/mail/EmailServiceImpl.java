package com.example.lms.mail;

import com.example.lms.smtp.SmtpCredentialProvider;
import com.example.lms.smtp.SmtpCredentialRouter;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
@Profile("cloud")
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
  private final SmtpCredentialRouter credentialRouter;

  public JavaMailSenderImpl getJavaMailSender() {
    SmtpCredentialProvider provider = credentialRouter.getProvider();
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost(provider.getHost());
    mailSender.setPort(provider.getPort());
    mailSender.setUsername(provider.getUsername());
    mailSender.setPassword(provider.getPassword());
    Properties javaMailProps = mailSender.getJavaMailProperties();
    javaMailProps.put("mail.smtp.auth", "true");
    javaMailProps.put("mail.smtp.starttls.enable", "true");

    log.info("Selected SMTP host: {}", provider.getHost());
    return mailSender;
  }

  @Override
  public void sendEmail(EmailDetails details) {
    var mailSender = getJavaMailSender();
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(details.getRecipient());
    message.setSubject(details.getSubject());
    message.setText(details.getMsgBody());
    mailSender.send(message);
  }
}
