package com.example.moneytools.service;

import com.example.moneytools.dto.MortgageRequest;
import com.example.moneytools.dto.MortgageResult;
import org.springframework.stereotype.Service;

@Service
public class MortgageCalculatorService {
    public MortgageResult calculate(MortgageRequest request) {
        double principal = request.getExpectedLoanAmount().doubleValue();
        double monthlyRate = request.getAnnualRate() / 100.0 / 12.0;
        int months = request.getYears() * 12;

        double monthlyPayment = monthlyPayment(principal, monthlyRate, months, request.getRepaymentType());
        double totalInterest = totalInterest(principal, monthlyRate, months, request.getRepaymentType());
        double totalPayment = principal + totalInterest;
        double requiredEquity = Math.max(0.0, request.getHousePrice().doubleValue() - principal);
        double cashShortfall = Math.max(0.0, requiredEquity - request.getCashOnHand().doubleValue());
        double maxLoanByLtv = request.getHousePrice().doubleValue() * request.getLtvRatio() / 100.0;
        double monthlyIncome = request.getAnnualIncome().doubleValue() / 12.0;
        double burdenRate = monthlyIncome == 0.0 ? 0.0 : monthlyPayment / monthlyIncome * 100.0;
        double burdenRateWithDebt = monthlyIncome == 0.0
                ? 0.0
                : (monthlyPayment + request.getExistingMonthlyDebtPayment().doubleValue()) / monthlyIncome * 100.0;
        String riskLabel = riskLabel(burdenRateWithDebt);

        return new MortgageResult(
                monthlyPayment,
                totalInterest,
                totalPayment,
                requiredEquity,
                cashShortfall,
                maxLoanByLtv,
                burdenRate,
                burdenRateWithDebt,
                riskLabel,
                riskLevel(riskLabel)
        );
    }

    private double monthlyPayment(double principal, double monthlyRate, int months, String repaymentType) {
        return switch (repaymentType) {
            case "EQUAL_PRINCIPAL" -> principal / months + principal * monthlyRate;
            case "BULLET" -> principal * monthlyRate;
            default -> equalPaymentMonthly(principal, monthlyRate, months);
        };
    }

    private double totalInterest(double principal, double monthlyRate, int months, String repaymentType) {
        return switch (repaymentType) {
            case "EQUAL_PRINCIPAL" -> equalPrincipalTotalInterest(principal, monthlyRate, months);
            case "BULLET" -> principal * monthlyRate * months;
            default -> equalPaymentMonthly(principal, monthlyRate, months) * months - principal;
        };
    }

    private double equalPaymentMonthly(double principal, double monthlyRate, int months) {
        if (monthlyRate == 0.0) {
            return principal / months;
        }
        double factor = Math.pow(1 + monthlyRate, months);
        return principal * monthlyRate * factor / (factor - 1);
    }

    private double equalPrincipalTotalInterest(double principal, double monthlyRate, int months) {
        double totalInterest = 0.0;
        double fixedPrincipal = principal / months;
        double remaining = principal;
        for (int month = 1; month <= months; month++) {
            totalInterest += remaining * monthlyRate;
            remaining = Math.max(0.0, remaining - fixedPrincipal);
        }
        return totalInterest;
    }

    private String riskLabel(double burdenRate) {
        if (burdenRate <= 25.0) {
            return "안전";
        }
        if (burdenRate <= 40.0) {
            return "주의";
        }
        return "위험";
    }

    private String riskLevel(String riskLabel) {
        return switch (riskLabel) {
            case "주의" -> "caution";
            case "위험" -> "danger";
            default -> "safe";
        };
    }
}
