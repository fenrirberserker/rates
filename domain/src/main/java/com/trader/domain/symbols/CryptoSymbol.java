package com.trader.domain.symbols;

public enum CryptoSymbol {
    BTC,
    ETH;

    public String ticker() {
        return name();
    }
}
