package com.example.moneytools.service;

import com.example.moneytools.dto.DividendRequest;
import com.example.moneytools.dto.DividendResult;
import org.springframework.stereotype.Service;

@Service
public class DividendCalculatorService {
    public DividendResult calculate(DividendRequest request) {
        int paymentsPerYear = switch (request.getPeriod()) {
            case "MONTHLY" -> 12;
            case "SEMI_ANNUAL" -> 2;
            case "ANNUAL" -> 1;
            default -> 4;
        };
        double gross = safe(request.getShares()) * safe(request.getDividendPerShare());
        double taxMultiplier = request.isTaxApplied() ? Math.max(0, 1 - safe(request.getTaxRate()) / 100.0) : 1.0;
        double net = gross * taxMultiplier;
        double annualGross = gross * paymentsPerYear;
        double annualNet = net * paymentsPerYear;
        return new DividendResult(gross, net, annualGross / 12.0, annualNet / 12.0, annualGross, annualNet, paymentsPerYear);
    }

    private double safe(Number value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}
