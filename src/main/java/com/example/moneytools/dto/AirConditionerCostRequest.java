package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AirConditionerCostRequest {
    @NotNull
    @DecimalMin("100.0")
    @DecimalMax("10000.0")
    private Double powerWatts = 1800.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("24.0")
    private Double hoursPerDay = 8.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double loadFactor = 1.0;

    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("31.0")
    private Double daysPerMonth = 30.0;

    @NotNull
    @DecimalMin("10.0")
    @DecimalMax("1000.0")
    private Double electricityRatePerKwh = 160.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1000.0")
    private Double standbyWatts = 7.5;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100000.0")
    private Double householdUsageKwh = 250.0;

    @NotNull
    @Pattern(regexp = "NORMAL|SUMMER|WINTER", message = "계절 구분을 선택해주세요.")
    private String season = "SUMMER";

    public Double getPowerWatts() {
        return powerWatts;
    }

    public void setPowerWatts(Double powerWatts) {
        this.powerWatts = powerWatts;
    }

    public Double getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(Double hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public Double getLoadFactor() {
        return loadFactor;
    }

    public void setLoadFactor(Double loadFactor) {
        this.loadFactor = loadFactor;
    }

    public Double getDaysPerMonth() {
        return daysPerMonth;
    }

    public void setDaysPerMonth(Double daysPerMonth) {
        this.daysPerMonth = daysPerMonth;
    }

    public Double getElectricityRatePerKwh() {
        return electricityRatePerKwh;
    }

    public void setElectricityRatePerKwh(Double electricityRatePerKwh) {
        this.electricityRatePerKwh = electricityRatePerKwh;
    }

    public Double getStandbyWatts() {
        return standbyWatts;
    }

    public void setStandbyWatts(Double standbyWatts) {
        this.standbyWatts = standbyWatts;
    }

    public Double getHouseholdUsageKwh() {
        return householdUsageKwh;
    }

    public void setHouseholdUsageKwh(Double householdUsageKwh) {
        this.householdUsageKwh = householdUsageKwh;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
