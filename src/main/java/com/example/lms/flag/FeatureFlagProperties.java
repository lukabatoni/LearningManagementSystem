package com.example.lms.flag;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feature-flag")
@Getter
@Setter
public class FeatureFlagProperties {
  private String uri;
  private String username;
  private String password;
}
