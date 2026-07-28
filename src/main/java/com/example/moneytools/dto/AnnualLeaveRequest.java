package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AnnualLeaveRequest {
    @NotNull
    @DecimalMin(value = "0.0", message = "수당 대상 미사용 연차일수는 0일 이상 입력해주세요.")
    @DecimalMax(value = "36500.0", message = "수당 대상 미사용 연차일수는 36,500일 이하로 입력해주세요.")
    private Double unusedLeaveDays = 5.0;

    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double dailyOrdinaryWage = 120000.0;

    public Double getUnusedLeaveDays() { return unusedLeaveDays; }
    public void setUnusedLeaveDays(Double unusedLeaveDays) { this.unusedLeaveDays = unusedLeaveDays; }
    public Double getDailyOrdinaryWage() { return dailyOrdinaryWage; }
    public void setDailyOrdinaryWage(Double dailyOrdinaryWage) { this.dailyOrdinaryWage = dailyOrdinaryWage; }
}
