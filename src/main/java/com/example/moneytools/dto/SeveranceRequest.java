package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SeveranceRequest {
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate = LocalDate.now().minusYears(3);

    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate = LocalDate.now();

    @NotNull @DecimalMin("0.0")
    private Double totalWageForLastThreeMonths = 10_500_000.0;

    @NotNull @DecimalMin("0.0")
    private Double annualBonus = 0.0;

    @NotNull @DecimalMin("0.0")
    private Double annualLeaveAllowance = 0.0;

    @NotNull @DecimalMin("0.0")
    private Double ordinaryDailyWage = 0.0;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Double getTotalWageForLastThreeMonths() { return totalWageForLastThreeMonths; }
    public void setTotalWageForLastThreeMonths(Double totalWageForLastThreeMonths) { this.totalWageForLastThreeMonths = totalWageForLastThreeMonths; }
    public Double getAnnualBonus() { return annualBonus; }
    public void setAnnualBonus(Double annualBonus) { this.annualBonus = annualBonus; }
    public Double getAnnualLeaveAllowance() { return annualLeaveAllowance; }
    public void setAnnualLeaveAllowance(Double annualLeaveAllowance) { this.annualLeaveAllowance = annualLeaveAllowance; }
    public Double getOrdinaryDailyWage() { return ordinaryDailyWage; }
    public void setOrdinaryDailyWage(Double ordinaryDailyWage) { this.ordinaryDailyWage = ordinaryDailyWage; }
}
