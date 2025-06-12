package com.example.lms.smtp;

import com.example.lms.flag.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpCredentialRouter {
  private final FeatureFlagService flagService;
  private final UpsCredentialProvider upsProvider;
  private final DestinationCredentialProvider destProvider;

  public SmtpCredentialProvider getProvider() {
    boolean flagEnabled = flagService.isFlagActive("dest_or_ups");
    return flagEnabled ? destProvider : upsProvider;
  }

}
