package com.example.moneytools.service;

import com.example.moneytools.dto.ElectricityBillRequest;
import com.example.moneytools.dto.ElectricityBillResult;
import org.springframework.stereotype.Service;

@Service
public class ElectricityBillCalculatorService {
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
        double climateCharge = usage * 9.0;
        double fuelAdjustment = usage * 5.0;
        double subtotal = baseFee + energyCharge + climateCharge + fuelAdjustment;
        double vat = Math.round(subtotal * 0.1);
        double fund = Math.floor(subtotal * 0.037 / 10.0) * 10.0;
        double total = subtotal + vat + fund;
        double avgUnitPrice = usage > 0 ? total / usage : 0.0;

        return new ElectricityBillResult(usage, baseFee, energyCharge, climateCharge, fuelAdjustment, subtotal, vat, fund, total, avgUnitPrice);
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
