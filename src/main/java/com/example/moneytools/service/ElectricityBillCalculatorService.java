package com.example.moneytools.service;

import com.example.moneytools.dto.ElectricityBillRequest;
import com.example.moneytools.dto.ElectricityBillResult;
import com.example.moneytools.util.RoundingPolicy;
import org.springframework.stereotype.Service;

@Service
public class ElectricityBillCalculatorService {
    private static final double CLIMATE_CHARGE_PER_KWH = 9.0;
    private static final double FUEL_ADJUSTMENT_PER_KWH = 5.0;
    private static final double VALUE_ADDED_TAX_RATE = 0.1;
    private static final double ELECTRIC_POWER_INDUSTRY_FUND_RATE = 0.027;

    public ElectricityBillResult calculate(ElectricityBillRequest request) {
        double usage = request.getUsageKwh();
        TierPolicy policy = policy(request.getSeason());

        double tier1Usage = Math.min(usage, policy.tier1Limit);
        double tier2Usage = Math.min(Math.max(usage - policy.tier1Limit, 0), policy.tier2Limit - policy.tier1Limit);
        double tier3Usage = Math.max(usage - policy.tier2Limit, 0);

        double energyCharge = tier1Usage * policy.rate1
                + tier2Usage * policy.rate2
                + tier3Usage * policy.rate3;
        double baseFee = usage <= policy.tier1Limit ? policy.baseFee1 : usage <= policy.tier2Limit ? policy.baseFee2 : policy.baseFee3;
        double climateCharge = usage * CLIMATE_CHARGE_PER_KWH;
        double fuelAdjustment = usage * FUEL_ADJUSTMENT_PER_KWH;
        double subtotal = baseFee + energyCharge + climateCharge + fuelAdjustment;
        double vat = RoundingPolicy.roundToWon(subtotal * VALUE_ADDED_TAX_RATE);
        double fund = RoundingPolicy.floorToTenWon(subtotal * ELECTRIC_POWER_INDUSTRY_FUND_RATE);
        double total = RoundingPolicy.floorToTenWon(subtotal + vat + fund);
        double avgUnitPrice = usage > 0 ? total / usage : 0.0;

        double previousUsage = request.getPreviousUsageKwh() == null ? 0.0 : request.getPreviousUsageKwh();
        double usageDelta = usage - previousUsage;
        return new ElectricityBillResult(usage, baseFee, energyCharge, climateCharge, fuelAdjustment, subtotal, vat, fund, total, avgUnitPrice, previousUsage, usageDelta);
    }

    private TierPolicy policy(String season) {
        return switch (season) {
            case "SUMMER" -> new TierPolicy(300, 450, 910, 1600, 7300, 120.0, 214.6, 307.3);
            case "WINTER" -> new TierPolicy(200, 400, 910, 1600, 7300, 120.0, 214.6, 307.3);
            default -> new TierPolicy(200, 400, 910, 1600, 7300, 120.0, 214.6, 307.3);
        };
    }

    private record TierPolicy(double tier1Limit, double tier2Limit, double baseFee1, double baseFee2, double baseFee3,
                              double rate1, double rate2, double rate3) {
    }
}
