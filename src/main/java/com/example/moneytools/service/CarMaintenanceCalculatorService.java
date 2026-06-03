package com.example.moneytools.service;

import com.example.moneytools.dto.CarMaintenanceRequest;
import com.example.moneytools.dto.CarMaintenanceResult;
import org.springframework.stereotype.Service;

@Service
public class CarMaintenanceCalculatorService {
    public CarMaintenanceResult calculate(CarMaintenanceRequest request) {
        double fuelCostMonthly = request.getMonthlyDistanceKm() / request.getFuelEfficiencyKmPerLiter() * request.getFuelPricePerLiter();
        double fixedCostMonthly = request.getParkingFeeMonthly()
                + request.getInsuranceAnnual() / 12.0
                + request.getTaxAnnual() / 12.0
                + request.getInstallmentMonthly();
        double variableCostMonthly = fuelCostMonthly + request.getMaintenanceAnnual() / 12.0 + request.getTollMonthly();
        double totalCostMonthly = fixedCostMonthly + variableCostMonthly;
        double totalCostAnnual = totalCostMonthly * 12.0;
        double costPerKm = request.getMonthlyDistanceKm() > 0 ? totalCostMonthly / request.getMonthlyDistanceKm() : 0.0;
        return new CarMaintenanceResult(fuelCostMonthly, fixedCostMonthly, variableCostMonthly, totalCostMonthly, totalCostAnnual, costPerKm);
    }
}
