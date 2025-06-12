package com.example.lms.smtp;

import com.example.lms.exception.MissingSmtpPropertyException;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.springframework.stereotype.Service;

@Service
public class DestinationCredentialProvider implements SmtpCredentialProvider {
  private static final String DESTINATION_NAME = "mail-dest";
  private static final String HOST_PROPERTY = "mail.smtp.host";
  private static final String PORT_PROPERTY = "mail.smtp.port";
  private static final String USERNAME_PROPERTY = "mail.user";
  private static final String PASSWORD_PROPERTY = "mail.password";

  private Destination getDestination() {
    return DestinationAccessor.getDestination(DESTINATION_NAME);
  }

  private String getProperty(String key) {
    return (String) getDestination()
        .get(key)
        .getOrElseThrow(() -> new MissingSmtpPropertyException(key));
  }

  @Override
  public String getHost() {
    return getProperty(HOST_PROPERTY);
  }

  @Override
  public int getPort() {
    return Integer.parseInt(getProperty(PORT_PROPERTY));
  }

  @Override
  public String getUsername() {
    return getProperty(USERNAME_PROPERTY);
  }

  @Override
  public String getPassword() {
    return getProperty(PASSWORD_PROPERTY);
  }
}
