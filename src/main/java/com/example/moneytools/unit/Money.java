package com.example.moneytools.unit;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, CurrencyCode currency) {
    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
    }
}
