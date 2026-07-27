package com.example.moneytools.dto;

import java.time.LocalDate;

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
        LocalDate breakEvenDate,
        String recommendation,
        String recommendationLevel
) {
}
