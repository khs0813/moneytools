package com.example.moneytools.service;

import com.example.moneytools.dto.AirConditionerCostRequest;
import com.example.moneytools.dto.AirConditionerCostResult;
import org.springframework.stereotype.Service;

@Service
public class AirConditionerCostCalculatorService {
    public AirConditionerCostResult calculate(AirConditionerCostRequest request) {
        double activeUsageKwh = request.getPowerWatts() / 1000.0 * request.getHoursPerDay() * request.getDaysPerMonth();
        double standbyUsageKwh = request.getStandbyWatts() / 1000.0 * 24.0 * request.getDaysPerMonth();
        double totalUsageKwh = activeUsageKwh + standbyUsageKwh;
        double estimatedCost = totalUsageKwh * request.getElectricityRatePerKwh();
        double dailyCost = estimatedCost / request.getDaysPerMonth();
        double hourlyCost = estimatedCost / (request.getHoursPerDay() * request.getDaysPerMonth());
        return new AirConditionerCostResult(activeUsageKwh, standbyUsageKwh, totalUsageKwh, estimatedCost, dailyCost, hourlyCost);
    }
}
