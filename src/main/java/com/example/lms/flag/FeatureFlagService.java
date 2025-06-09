package com.example.lms.flag;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {
  private final FeatureFlagProperties flagProperties;
  private final RestTemplate restTemplate;

  public boolean isFlagActive(String featureName) {
    String evaluateUrlFormat = "%s/api/v1/evaluate/%s";
    String evaluationUri = String.format(evaluateUrlFormat, flagProperties.getUri(), featureName);
    String credentials = Base64.encodeBase64String(
        (flagProperties.getUsername() + ":" + flagProperties.getPassword()).getBytes()
    );

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + credentials);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<String> response = restTemplate.exchange(
          evaluationUri, HttpMethod.GET, entity, String.class
      );
      return response.getStatusCode() == HttpStatus.OK;
    } catch (Exception e) {
      return false;
    }
  }

}
