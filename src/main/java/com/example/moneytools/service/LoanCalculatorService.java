package com.example.moneytools.service;

import com.example.moneytools.dto.LoanPaymentRow;
import com.example.moneytools.dto.LoanRequest;
import com.example.moneytools.dto.LoanResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanCalculatorService {
    public LoanResult calculate(LoanRequest request) {
        double principal = request.getPrincipal().doubleValue();
        double monthlyRate = request.getAnnualRate() / 100.0 / 12.0;
        int months = request.getYears() * 12;
        List<LoanPaymentRow> schedule = switch (request.getRepaymentType()) {
            case "EQUAL_PRINCIPAL" -> equalPrincipal(principal, monthlyRate, months);
            case "BULLET" -> bullet(principal, monthlyRate, months);
            default -> equalPayment(principal, monthlyRate, months);
        };
        double totalPayment = schedule.stream().mapToDouble(LoanPaymentRow::payment).sum();
        double totalInterest = schedule.stream().mapToDouble(LoanPaymentRow::interestPayment).sum();
        double firstPayment = schedule.isEmpty() ? 0.0 : schedule.get(0).payment();
        double averagePayment = months == 0 ? 0.0 : totalPayment / months;
        return new LoanResult(firstPayment, averagePayment, totalInterest, totalPayment, months, schedule);
    }

    private List<LoanPaymentRow> equalPayment(double principal, double monthlyRate, int months) {
        List<LoanPaymentRow> rows = new ArrayList<>();
        double remaining = principal;
        double monthlyPayment = monthlyRate == 0
                ? principal / months
                : principal * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1);
        for (int month = 1; month <= months; month++) {
            double interest = remaining * monthlyRate;
            double principalPayment = Math.min(remaining, monthlyPayment - interest);
            remaining = Math.max(0, remaining - principalPayment);
            double payment = principalPayment + interest;
            rows.add(new LoanPaymentRow(month, payment, principalPayment, interest, remaining));
        }
        return rows;
    }

    private List<LoanPaymentRow> equalPrincipal(double principal, double monthlyRate, int months) {
        List<LoanPaymentRow> rows = new ArrayList<>();
        double remaining = principal;
        double fixedPrincipal = principal / months;
        for (int month = 1; month <= months; month++) {
            double interest = remaining * monthlyRate;
            double principalPayment = Math.min(remaining, fixedPrincipal);
            remaining = Math.max(0, remaining - principalPayment);
            double payment = principalPayment + interest;
            rows.add(new LoanPaymentRow(month, payment, principalPayment, interest, remaining));
        }
        return rows;
    }

    private List<LoanPaymentRow> bullet(double principal, double monthlyRate, int months) {
        List<LoanPaymentRow> rows = new ArrayList<>();
        for (int month = 1; month <= months; month++) {
            double interest = principal * monthlyRate;
            double principalPayment = month == months ? principal : 0.0;
            double payment = interest + principalPayment;
            double remaining = month == months ? 0.0 : principal;
            rows.add(new LoanPaymentRow(month, payment, principalPayment, interest, remaining));
        }
        return rows;
    }
}
