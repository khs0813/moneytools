package com.example.moneytools.dto;

public record SeveranceResult(
        long serviceDays,
        long calculationPeriodDays,
        double averageDailyWage,
        double ordinaryDailyWage,
        double appliedDailyWage,
        double estimatedSeverancePay
) {
}
