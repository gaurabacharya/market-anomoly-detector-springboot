package com.quotemediaexample.demo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.Instant;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import com.quotemediaexample.demo.model.Tick;
import com.quotemediaexample.demo.util.ZScoreWindow;

@Component
@RequiredArgsConstructor
public class TickProcessor implements CommandLineRunner {
  private final AnomalyService anomalySvc;
  private final MerlionClient merlionClient;

  @Override
  public void run(String... args) throws Exception {
    List<Tick> tickList = new ArrayList<>();

    try (Reader r = Files.newBufferedReader(Paths.get("src/main/resources/sample-ticks.csv"));
         CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(r)) {

      Map<String,ZScoreWindow> windows = new HashMap<>();

      for (CSVRecord rec : parser) {
        Tick t = Tick.from(rec); // ts,symbol,price
        tickList.add(t);
      }

      List<Instant> timestamps = tickList.stream()
          .map(Tick::getTimestamp)
          .toList();

      List<Double> prices = tickList.stream()
          .map(Tick::getPrice)
          .toList();

      try {
        List<Double> scores = merlionClient.detectAnomalies(timestamps, prices, "AAPL");
        System.out.println("scores size = " + scores.size());
        System.out.println("tick size = " + tickList.size());

        for (int i = 0; i < scores.size(); i++) {
          double score = scores.get(i);
          if (score > 3) {
            Tick t = tickList.get(i);
            anomalySvc.record(t, score);
            System.out.println("Anomaly: " + t + " → score=" + score);
          }
        }
      } catch (Exception e) {
        System.err.println("Failed to call Merlion: " + e.getMessage());
      }
    }
  }
}
