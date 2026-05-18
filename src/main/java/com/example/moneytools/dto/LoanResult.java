package com.example.moneytools.dto;

import java.util.List;

public record LoanResult(
        double firstMonthlyPayment,
        double averageMonthlyPayment,
        double totalInterest,
        double totalPayment,
        int totalMonths,
        List<LoanPaymentRow> schedule
) {
}
