package com.example.moneytools.unit;

public enum CurrencyCode {
    KRW,
    USD,
    JPY,
    EUR,
    CNY;

    public boolean isKrw() {
        return this == KRW;
    }
}
