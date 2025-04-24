package com.quotemediaexample.demo.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.quotemediaexample.demo.model.Tick;

@Service
public class AnomalyService {
    private final ConcurrentHashMap<String, SymbolState> symbolStates = new ConcurrentHashMap<>();
    private final List<AnomalyRecord> anomalyRecords = new ArrayList<>();
    private static final String DATA_DIR = "data";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void record(Tick tick, double zScore) {
        SymbolState state = symbolStates.computeIfAbsent(tick.getSymbol(), s -> new SymbolState(s));
        state.update(zScore);
        anomalyRecords.add(new AnomalyRecord(tick, zScore));
    }

    public SymbolState latestState(String symbol) {
        return symbolStates.getOrDefault(symbol, new SymbolState(symbol));
    }

    public void writeCsv(PrintWriter writer) {
        writer.println("timestamp,symbol,price,zscore");
        System.out.println("Exporting " + anomalyRecords.size() + " anomalies");
        for (AnomalyRecord record : anomalyRecords) {
            writer.printf("%s,%s,%.2f,%.2f%n",
                record.tick().getTimestamp(),
                record.tick().getSymbol(),
                record.tick().getPrice(),
                record.zScore());
        }
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void persistAndReset() {
        String date = LocalDate.now().format(DATE_FORMAT);
        String filename = String.format("%s/anomalies_%s.csv", DATA_DIR, date);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writeCsv(writer);
        } catch (IOException e) {
            // Log error but don't throw to prevent scheduler from failing
            e.printStackTrace();
        }

        // Reset collections for new day
        symbolStates.clear();
        anomalyRecords.clear();
    }

    public record AnomalyRecord(Tick tick, double zScore) {}
}

