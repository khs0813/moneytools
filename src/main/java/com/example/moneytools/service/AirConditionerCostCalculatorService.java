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
        double activeUsageKwh = request.getPowerWatts() / 1000.0 * request.getHoursPerDay() * request.getDaysPerMonth();
        double standbyUsageKwh = request.getStandbyWatts() / 1000.0 * 24.0 * request.getDaysPerMonth();
        double totalUsageKwh = activeUsageKwh + standbyUsageKwh;
        double estimatedCost = totalUsageKwh * request.getElectricityRatePerKwh();
        double dailyCost = estimatedCost / request.getDaysPerMonth();
        double hourlyCost = estimatedCost / (request.getHoursPerDay() * request.getDaysPerMonth());
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
}
