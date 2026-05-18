package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class AnnualLeaveRequest {
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate = LocalDate.now().minusYears(2);

    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate calculationDate = LocalDate.now();

    @NotNull @Min(0)
    private Integer usedLeaveDays = 5;

    @NotNull @DecimalMin("0.0")
    private Double dailyOrdinaryWage = 120000.0;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDate calculationDate) { this.calculationDate = calculationDate; }
    public Integer getUsedLeaveDays() { return usedLeaveDays; }
    public void setUsedLeaveDays(Integer usedLeaveDays) { this.usedLeaveDays = usedLeaveDays; }
    public Double getDailyOrdinaryWage() { return dailyOrdinaryWage; }
    public void setDailyOrdinaryWage(Double dailyOrdinaryWage) { this.dailyOrdinaryWage = dailyOrdinaryWage; }
}
