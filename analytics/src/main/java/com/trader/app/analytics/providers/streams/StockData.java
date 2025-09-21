package com.trader.app.analytics.providers.streams;

import java.time.LocalDateTime;

public class StockData {
    private String symbol;
    private Double high;
    private Double low;
    private Double open;
    private Double close;
    private Long volume;
    private LocalDateTime timestamp;

    public StockData() {}

    public StockData(String symbol, Double high, Double low, Double open, Double close, Long volume) {
        this.symbol = symbol;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
        this.volume = volume;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Double getHigh() { return high; }
    public void setHigh(Double high) { this.high = high; }

    public Double getLow() { return low; }
    public void setLow(Double low) { this.low = low; }

    public Double getOpen() { return open; }
    public void setOpen(Double open) { this.open = open; }

    public Double getClose() { return close; }
    public void setClose(Double close) { this.close = close; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "StockData{" +
                "symbol='" + symbol + '\'' +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", timestamp=" + timestamp +
                '}';
    }
}