package com.trader.app.core.providers.streams;

import java.time.LocalDateTime;

public record StockData(
    String symbol,
    Double high,
    Double low,
    Double open,
    Double close,
    Long volume,
    LocalDateTime timestamp
) {
    public StockData(String symbol, Double high, Double low, Double open, Double close, Long volume) {
        this(symbol, high, low, open, close, volume, LocalDateTime.now());
    }
}
