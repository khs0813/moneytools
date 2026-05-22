package com.example.moneytools.dto;

public record MortgageResult(
        double estimatedMonthlyPayment,
        double totalInterest,
        double totalPayment,
        double requiredEquity,
        double cashShortfall,
        double maxLoanByLtv,
        double monthlyBurdenRate,
        double monthlyBurdenRateWithExistingDebt,
        String riskLabel,
        String riskLevel
) {
}
