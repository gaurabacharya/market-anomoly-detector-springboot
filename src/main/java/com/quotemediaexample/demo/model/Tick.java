package com.quotemediaexample.demo.model;

import org.apache.commons.csv.CSVRecord;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import org.apache.commons.csv.CSVRecord;

public class Tick {

    private Instant timestamp;
    private String symbol;
    private double price;

    public Tick(Instant timestamp, String symbol, double price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    public static Tick from(CSVRecord record) {
        return new Tick(
            Instant.parse(record.get("timestamp")),
            record.get("symbol"),
            Double.parseDouble(record.get("price"))
        );
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Optional: toString() for easy logging
    @Override
    public String toString() {
        return "Tick{" +
            "timestamp=" + timestamp +
            ", symbol='" + symbol + '\'' +
            ", price=" + price +
            '}';
    }
}
