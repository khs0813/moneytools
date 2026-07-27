package com.example.moneytools.unit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public record Percentage(BigDecimal value) {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public Percentage {
        Objects.requireNonNull(value, "value");
    }

    public static Percentage ofPercent(double percent) {
        return new Percentage(BigDecimal.valueOf(percent));
    }

    public BigDecimal fraction() {
        return value.divide(ONE_HUNDRED, MathContext.DECIMAL64);
    }
}
