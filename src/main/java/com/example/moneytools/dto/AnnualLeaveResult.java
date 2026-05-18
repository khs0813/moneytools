package com.example.moneytools.dto;

public record AnnualLeaveResult(
        long serviceMonths,
        int generatedLeaveDays,
        int usedLeaveDays,
        int remainingLeaveDays,
        double estimatedAllowance
) {
}
