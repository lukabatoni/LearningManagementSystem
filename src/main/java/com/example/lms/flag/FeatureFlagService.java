package com.example.lms.flag;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {
  private final FeatureFlagProperties flagProperties;

  public boolean isFlagActive(String featureName) {
    String evaluateUrlFormat = "%s/api/v1/evaluate/%s";
    String evaluationUri = String.format(evaluateUrlFormat, flagProperties.getUri(), featureName);
    String credentials = Base64.encodeBase64String(
        (flagProperties.getUsername() + ":" + flagProperties.getPassword()).getBytes()
    );

    try {
      HttpClient httpClient = HttpClientBuilder.create().build();
      HttpUriRequest evaluateRequest = new HttpGet(evaluationUri);
      evaluateRequest.addHeader("Authorization", "Basic " + credentials);

      HttpResponse response = httpClient.execute(evaluateRequest);
      int statusCode = response.getStatusLine().getStatusCode();
      return statusCode == 200;
    } catch (Exception e) {
      return false;
    }
  }

}
