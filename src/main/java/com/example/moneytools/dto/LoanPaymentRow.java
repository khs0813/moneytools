package com.example.moneytools.dto;

public record LoanPaymentRow(
        int month,
        double payment,
        double principalPayment,
        double interestPayment,
        double remainingPrincipal
) {
}
