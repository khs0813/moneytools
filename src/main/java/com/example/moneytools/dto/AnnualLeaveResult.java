package com.example.moneytools.dto;

public record AnnualLeaveResult(
        double unusedLeaveDays,
        double dailyOrdinaryWage,
        double estimatedAllowance
) {
}
