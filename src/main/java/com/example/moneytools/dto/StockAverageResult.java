package com.example.moneytools.dto;

public record StockAverageResult(
        double currentInvestment,
        double additionalInvestment,
        long totalShares,
        double totalInvestment,
        double newAveragePrice,
        double averagePriceChange,
        double averagePriceChangeRate,
        double breakEvenPrice
) {
}
