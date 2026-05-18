package com.example.moneytools.service;

import com.example.moneytools.dto.SeveranceRequest;
import com.example.moneytools.dto.SeveranceResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class SeveranceCalculatorService {
    public SeveranceResult calculate(SeveranceRequest request) {
        long serviceDays = Math.max(0, ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1);

        LocalDate periodStart = request.getEndDate().minusMonths(3);
        long calculationPeriodDays = Math.max(1, ChronoUnit.DAYS.between(periodStart, request.getEndDate()));

        double bonusIncluded = request.getAnnualBonus() * 3.0 / 12.0;
        double leaveIncluded = request.getAnnualLeaveAllowance() * 3.0 / 12.0;
        double threeMonthTotal = request.getTotalWageForLastThreeMonths() + bonusIncluded + leaveIncluded;

        double averageDailyWage = threeMonthTotal / calculationPeriodDays;
        double ordinaryDailyWage = Math.max(0, request.getOrdinaryDailyWage());
        double appliedDailyWage = Math.max(averageDailyWage, ordinaryDailyWage);
        double severance = serviceDays < 365 ? 0.0 : appliedDailyWage * 30.0 * serviceDays / 365.0;

        return new SeveranceResult(
                serviceDays,
                calculationPeriodDays,
                averageDailyWage,
                ordinaryDailyWage,
                appliedDailyWage,
                severance
        );
    }
}
