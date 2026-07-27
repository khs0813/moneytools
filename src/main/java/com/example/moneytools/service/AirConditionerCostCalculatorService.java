package com.example.moneytools.service;

import com.example.moneytools.dto.AirConditionerCostRequest;
import com.example.moneytools.dto.AirConditionerCostResult;
import com.example.moneytools.dto.ElectricityBillRequest;
import org.springframework.stereotype.Service;

@Service
public class AirConditionerCostCalculatorService {
    private final ElectricityBillCalculatorService electricityBillCalculatorService;

    public AirConditionerCostCalculatorService(ElectricityBillCalculatorService electricityBillCalculatorService) {
        this.electricityBillCalculatorService = electricityBillCalculatorService;
    }

    public AirConditionerCostResult calculate(AirConditionerCostRequest request) {
        double activeHours = clamp(request.getHoursPerDay(), 0.0, 24.0);
        double standbyHours = 24.0 - activeHours;
        double days = Math.max(0.0, request.getDaysPerMonth());
        double loadFactor = clamp(request.getLoadFactor(), 0.0, 1.0);
        double activeUsageKwh = request.getPowerWatts() / 1000.0 * activeHours * days * loadFactor;
        double standbyUsageKwh = request.getStandbyWatts() / 1000.0 * standbyHours * days;
        double totalUsageKwh = activeUsageKwh + standbyUsageKwh;
        double estimatedCost = totalUsageKwh * request.getElectricityRatePerKwh();
        double dailyCost = days > 0.0 ? estimatedCost / days : 0.0;
        double activeHourCount = activeHours * days;
        double hourlyCost = activeHourCount > 0.0 ? estimatedCost / activeHourCount : 0.0;
        double householdBaseUsage = request.getHouseholdUsageKwh();
        double householdTotalUsage = householdBaseUsage + totalUsageKwh;
        double householdBaseBill = calculateHouseholdBill(householdBaseUsage, request.getSeason());
        double householdTotalBill = calculateHouseholdBill(householdTotalUsage, request.getSeason());
        double householdIncrementalCost = Math.max(0.0, householdTotalBill - householdBaseBill);
        return new AirConditionerCostResult(
                activeUsageKwh,
                standbyUsageKwh,
                totalUsageKwh,
                estimatedCost,
                dailyCost,
                hourlyCost,
                householdBaseUsage,
                householdTotalUsage,
                householdBaseBill,
                householdTotalBill,
                householdIncrementalCost
        );
    }

    private double calculateHouseholdBill(double usageKwh, String season) {
        ElectricityBillRequest request = new ElectricityBillRequest();
        request.setUsageKwh(Math.max(0.0, usageKwh));
        request.setPreviousUsageKwh(0.0);
        request.setSeason(season);
        return electricityBillCalculatorService.calculate(request).totalBill();
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
