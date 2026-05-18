package com.example.moneytools.dto;

public record SalaryResult(
        double grossMonthly,
        double nationalPension,
        double healthInsurance,
        double longTermCareInsurance,
        double employmentInsurance,
        double incomeTax,
        double localIncomeTax,
        double totalDeduction,
        double netMonthly,
        double netAnnual
) {
}
