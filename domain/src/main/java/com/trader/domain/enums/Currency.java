package com.trader.domain.enums;

public enum Currency {

    USD("USD"),
    CAD("CAD"),
    EUR("EUR");

    public final String value;

    Currency(String value) {
        this.value = value;
    }
}