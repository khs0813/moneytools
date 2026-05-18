package com.example.moneytools.service;

import com.example.moneytools.dto.AnnualLeaveRequest;
import com.example.moneytools.dto.AnnualLeaveResult;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class AnnualLeaveCalculatorService {
    public AnnualLeaveResult calculate(AnnualLeaveRequest request) {
        long months = Math.max(0, ChronoUnit.MONTHS.between(request.getStartDate(), request.getCalculationDate()));
        int generated = generatedLeaveDays(months);
        int used = Math.max(0, request.getUsedLeaveDays());
        int remaining = Math.max(0, generated - used);
        double allowance = remaining * request.getDailyOrdinaryWage();
        return new AnnualLeaveResult(months, generated, used, remaining, allowance);
    }

    private int generatedLeaveDays(long serviceMonths) {
        if (serviceMonths < 12) {
            return (int) Math.min(11, serviceMonths);
        }
        long years = serviceMonths / 12;
        int additional = (int) Math.max(0, (years - 1) / 2);
        return Math.min(25, 15 + additional);
    }
}
