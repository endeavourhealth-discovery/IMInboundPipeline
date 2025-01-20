package org.endeavourhealth.im_inbound_pipeline.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@RestController
public class QueueController {

  private static final String RABBITMQ_ENDPOINT = System.getenv("RABBITMQ_ENDPOINT");
  private static final String RABBITMQ_AUTH = System.getenv("RABBITMQ_AUTH");

  @GetMapping("/queue")
  private String getQueues() throws Exception {
    String endpoint = RABBITMQ_ENDPOINT +  "/queues";
    URL url = new URL(endpoint);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    String encodedAuth = Base64.getEncoder().encodeToString(RABBITMQ_AUTH.getBytes());
    connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
    int responseCode = connection.getResponseCode();
    if (responseCode == 200) {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
          response.append(line);
        }
        return response.toString();
      }
    } else {
      throw new Exception("Failed to fetch queues. HTTP Response Code: " + responseCode);
    }
  }
}
