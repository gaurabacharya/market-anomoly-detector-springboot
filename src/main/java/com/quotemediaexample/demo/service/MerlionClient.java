package com.quotemediaexample.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

@Service
public class MerlionClient {

    @Value("${merlion.api.url:http://localhost:8001}")
    private String merlionUrl;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Double> detectAnomalies(List<Instant> timestamps, List<Double> prices, String symbol) throws IOException, InterruptedException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("symbol", symbol);
        payload.put("timestamps", timestamps.stream().map(Instant::toString).toList());
        payload.put("prices", prices);

        String json = mapper.writeValueAsString(payload);

        System.out.println("Sending timestamps: " + timestamps.size());
        System.out.println("Sending prices: " + prices.size());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(merlionUrl + "/detect"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Merlion raw response: " + response.body());
        JsonNode jsonResponse = mapper.readTree(response.body());

        List<Double> scores = new ArrayList<>();
        for (JsonNode score : jsonResponse.get("anomaly_scores")) {
            scores.add(score.asDouble());
        }

        return scores;
    }


}