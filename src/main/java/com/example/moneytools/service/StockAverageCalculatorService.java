package com.example.moneytools.service;

import com.example.moneytools.dto.StockAverageRequest;
import com.example.moneytools.dto.StockAverageResult;
import org.springframework.stereotype.Service;

@Service
public class StockAverageCalculatorService {
    public StockAverageResult calculate(StockAverageRequest request) {
        long currentShares = Math.max(0, safe(request.getCurrentShares()).longValue());
        long additionalShares = Math.max(0, safe(request.getAdditionalShares()).longValue());
        double currentAveragePrice = Math.max(0.0, safe(request.getCurrentAveragePrice()).doubleValue());
        double additionalPrice = Math.max(0.0, safe(request.getAdditionalPrice()).doubleValue());

        double currentInvestment = currentShares * currentAveragePrice;
        double additionalInvestment = additionalShares * additionalPrice;
        long totalShares = currentShares + additionalShares;
        double totalInvestment = currentInvestment + additionalInvestment;
        double newAveragePrice = totalShares == 0 ? 0.0 : totalInvestment / totalShares;
        double averagePriceChange = newAveragePrice - currentAveragePrice;
        double averagePriceChangeRate = currentAveragePrice == 0.0 ? 0.0 : averagePriceChange / currentAveragePrice * 100.0;

        return new StockAverageResult(
                currentInvestment,
                additionalInvestment,
                totalShares,
                totalInvestment,
                newAveragePrice,
                averagePriceChange,
                averagePriceChangeRate,
                newAveragePrice
        );
    }

    private Number safe(Number value) {
        return value == null ? 0.0 : value;
    }
}
