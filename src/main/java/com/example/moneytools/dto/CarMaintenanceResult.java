package com.example.moneytools.dto;

public record CarMaintenanceResult(
        double fuelCostMonthly,
        double fixedCostMonthly,
        double variableCostMonthly,
        double totalCostMonthly,
        double totalCostAnnual,
        double costPerKm
) {
}
