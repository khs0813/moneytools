package com.example.moneytools.service;

import com.example.moneytools.dto.AnnualLeaveRequest;
import com.example.moneytools.dto.AnnualLeaveResult;
import org.springframework.stereotype.Service;

@Service
public class AnnualLeaveCalculatorService {
    public AnnualLeaveResult calculate(AnnualLeaveRequest request) {
        double unusedLeaveDays = Math.max(0.0, request.getUnusedLeaveDays());
        double dailyOrdinaryWage = Math.max(0.0, request.getDailyOrdinaryWage());
        double allowance = unusedLeaveDays * dailyOrdinaryWage;
        return new AnnualLeaveResult(unusedLeaveDays, dailyOrdinaryWage, allowance);
    }
}
