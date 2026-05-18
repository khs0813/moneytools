package com.example.moneytools.service;

import com.example.moneytools.dto.FairValueRequest;
import com.example.moneytools.dto.FairValueResult;
import org.springframework.stereotype.Service;

@Service
public class FairValueCalculatorService {
    public FairValueResult calculate(FairValueRequest request) {
        double base = request.getEps() * request.getTargetPer();
        double growthAdjusted = base * (1 + request.getGrowthRate() / 100.0) / (1 + request.getDiscountRate() / 100.0);
        double safeBuy = growthAdjusted * (1 - request.getSafetyMargin() / 100.0);
        return new FairValueResult(base, growthAdjusted, safeBuy, safeBuy * 0.85, safeBuy, safeBuy * 1.15);
    }
}
