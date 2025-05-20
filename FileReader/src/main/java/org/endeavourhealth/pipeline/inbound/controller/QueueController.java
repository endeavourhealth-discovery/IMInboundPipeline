package org.endeavourhealth.pipeline.inbound.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

@RestController
public class QueueController {

  private static final String RABBITMQ_HOST = System.getenv("RABBITMQ_HOST");
  private static final String RABBITMQ_USER = System.getenv("RABBITMQ_USER");
  private static final String RABBITMQ_PASSWORD = System.getenv("RABBITMQ_PASSWORD");
  private static final String RABBITMQ_PORT = System.getenv("RABBITMQ_PORT");

  @GetMapping("/queue")
  public String getQueues() throws IOException, URISyntaxException {
    String endpoint = RABBITMQ_HOST + ":" + RABBITMQ_PORT + "/api/queues";
    URL url = new URI(endpoint).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    String encodedAuth = Base64.getEncoder().encodeToString((RABBITMQ_USER + ":" + RABBITMQ_PASSWORD).getBytes());
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
      throw new IOException("Failed to fetch queues. HTTP Response Code: " + responseCode);
    }
  }
}
