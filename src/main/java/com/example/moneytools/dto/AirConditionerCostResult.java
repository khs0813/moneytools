package com.example.moneytools.dto;

public record AirConditionerCostResult(
        double activeUsageKwh,
        double standbyUsageKwh,
        double totalUsageKwh,
        double estimatedCost,
        double dailyCost,
        double hourlyCost
) {
}
