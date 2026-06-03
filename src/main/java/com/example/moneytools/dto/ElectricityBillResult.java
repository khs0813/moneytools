package com.example.moneytools.dto;

public record ElectricityBillResult(
        double usageKwh,
        double baseFee,
        double energyCharge,
        double climateCharge,
        double fuelAdjustment,
        double subtotal,
        double vat,
        double industryFund,
        double totalBill,
        double averageUnitPrice
) {
}
