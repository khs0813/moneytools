package com.example.moneytools.util;

public final class RoundingPolicy {
    private RoundingPolicy() {
    }

    public static double roundToWon(double value) {
        return Math.round(value);
    }

    public static double floorToTenWon(double value) {
        return Math.floor(value / 10.0) * 10.0;
    }
}
