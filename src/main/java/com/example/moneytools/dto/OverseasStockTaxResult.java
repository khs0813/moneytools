package com.example.moneytools.dto;

public record OverseasStockTaxResult(
        double buyAmountKrw,
        double sellAmountKrw,
        double capitalGainKrw,
        double taxableCapitalGainKrw,
        double capitalGainsTaxKrw,
        double dividendKrw,
        double dividendTaxKrw,
        double totalTaxKrw,
        double afterTaxProfitKrw
) {
}
