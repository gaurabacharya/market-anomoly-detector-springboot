package com.quotemediaexample.demo.service;

import org.springframework.beans.factory.annotation.Value;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quotemediaexample.demo.model.Tick;

import netscape.javascript.JSException;

public class SlackNotifier {
  @Value("${slack.webhook}")
  private String hook;

  private final HttpClient client = HttpClient.newHttpClient();
  private final ObjectMapper m = new ObjectMapper();

  public void sendSlackAlert(Tick t, double z) {
    Map<String,String> payload = Map.of(
      "text", String.format(":rotating_light: %s price spike! z=%.1f  (%.2f)", t.getSymbol(), z, t.getPrice())
    );

    try {
    HttpRequest req = HttpRequest.newBuilder(URI.create(hook))
      .header("Content-Type","application/json")
      .POST(HttpRequest.BodyPublishers.ofString(m.writeValueAsString(payload)))
      .build();
    client.sendAsync(req, BodyHandlers.discarding());
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }

  }
}
