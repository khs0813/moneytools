package com.example.moneytools.dto;

public record FairValueResult(
        double baseFairValue,
        double growthAdjustedFairValue,
        double safeBuyPrice,
        double conservativePrice,
        double neutralPrice,
        double aggressivePrice
) {
}
