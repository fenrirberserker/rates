package com.trader.domain.model;

import java.time.LocalDateTime;

public record StockData(
    String symbol,
    String type,
    Double high,
    Double low,
    Double open,
    Double close,
    Long volume,
    LocalDateTime timestamp
) {
    /** Used by providers — type is resolved later by the producer. */
    public StockData(String symbol, Double high, Double low, Double open, Double close, Long volume) {
        this(symbol, "UNKNOWN", high, low, open, close, volume, LocalDateTime.now());
    }

    /** Used by the producer when type is known but timestamp is not needed explicitly. */
    public StockData(String symbol, String type, Double high, Double low, Double open, Double close, Long volume) {
        this(symbol, type, high, low, open, close, volume, LocalDateTime.now());
    }

    /** Returns a copy of this record with the given type set. */
    public StockData withType(String type) {
        return new StockData(symbol, type, high, low, open, close, volume, timestamp);
    }
}