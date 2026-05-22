package com.example.moneytools.service;

import com.example.moneytools.dto.LoanRefinanceRequest;
import com.example.moneytools.dto.LoanRefinanceResult;
import org.springframework.stereotype.Service;

@Service
public class LoanRefinanceCalculatorService {
    public LoanRefinanceResult calculate(LoanRefinanceRequest request) {
        double balance = request.getCurrentBalance().doubleValue();
        int currentMonths = request.getCurrentRemainingYears() * 12;
        int newMonths = request.getNewYears() * 12;
        double currentMonthlyRate = request.getCurrentAnnualRate() / 100.0 / 12.0;
        double newMonthlyRate = request.getNewAnnualRate() / 100.0 / 12.0;

        double currentMonthlyPayment = monthlyPayment(balance, currentMonthlyRate, currentMonths, request.getRepaymentType());
        double newMonthlyPayment = monthlyPayment(balance, newMonthlyRate, newMonths, request.getRepaymentType());
        double monthlySavings = currentMonthlyPayment - newMonthlyPayment;
        double currentTotalInterest = totalInterest(balance, currentMonthlyRate, currentMonths, request.getRepaymentType());
        double newTotalInterest = totalInterest(balance, newMonthlyRate, newMonths, request.getRepaymentType());
        double totalInterestSavings = currentTotalInterest - newTotalInterest;
        double prepaymentPenalty = balance * request.getPrepaymentPenaltyRate() / 100.0;
        double totalSwitchingCost = prepaymentPenalty + request.getAdditionalCost().doubleValue();
        double netSavings = totalInterestSavings - totalSwitchingCost;
        int breakEvenMonths = monthlySavings > 0.0 ? (int) Math.ceil(totalSwitchingCost / monthlySavings) : -1;
        String recommendation = recommendation(netSavings, breakEvenMonths);

        return new LoanRefinanceResult(
                currentMonthlyPayment,
                newMonthlyPayment,
                monthlySavings,
                currentTotalInterest,
                newTotalInterest,
                totalInterestSavings,
                prepaymentPenalty,
                totalSwitchingCost,
                netSavings,
                breakEvenMonths,
                recommendation,
                recommendationLevel(recommendation)
        );
    }

    private double monthlyPayment(double principal, double monthlyRate, int months, String repaymentType) {
        if ("EQUAL_PRINCIPAL".equals(repaymentType)) {
            return principal / months + principal * monthlyRate;
        }
        if (monthlyRate == 0.0) {
            return principal / months;
        }
        double factor = Math.pow(1 + monthlyRate, months);
        return principal * monthlyRate * factor / (factor - 1);
    }

    private double totalInterest(double principal, double monthlyRate, int months, String repaymentType) {
        if ("EQUAL_PRINCIPAL".equals(repaymentType)) {
            double totalInterest = 0.0;
            double fixedPrincipal = principal / months;
            double remaining = principal;
            for (int month = 1; month <= months; month++) {
                totalInterest += remaining * monthlyRate;
                remaining = Math.max(0.0, remaining - fixedPrincipal);
            }
            return totalInterest;
        }
        return monthlyPayment(principal, monthlyRate, months, repaymentType) * months - principal;
    }

    private String recommendation(double netSavings, int breakEvenMonths) {
        if (netSavings <= 0.0) {
            return "갈아타기 불리";
        }
        if (breakEvenMonths > 0 && breakEvenMonths <= 24) {
            return "갈아타기 유리";
        }
        return "장기 보유 시 유리";
    }

    private String recommendationLevel(String recommendation) {
        return switch (recommendation) {
            case "갈아타기 유리" -> "safe";
            case "장기 보유 시 유리" -> "caution";
            default -> "danger";
        };
    }
}
