package com.example.moneytools.dto;

public record DividendResult(
        double oneTimeGross,
        double oneTimeNet,
        double monthlyGross,
        double monthlyNet,
        double annualGross,
        double annualNet,
        int paymentsPerYear
) {
}
