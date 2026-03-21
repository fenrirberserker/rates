package com.trader.domain.symbols;

public enum StockSymbol {
    TSLA,
    GOOGL,
    AMZN;

    public String ticker() {
        return name();
    }
}