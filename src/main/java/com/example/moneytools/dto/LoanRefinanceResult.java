package com.example.moneytools.dto;

public record LoanRefinanceResult(
        double currentMonthlyPayment,
        double newMonthlyPayment,
        double monthlySavings,
        double currentTotalInterest,
        double newTotalInterest,
        double totalInterestSavings,
        double prepaymentPenalty,
        double totalSwitchingCost,
        double netSavings,
        int breakEvenMonths,
        String recommendation,
        String recommendationLevel
) {
}
