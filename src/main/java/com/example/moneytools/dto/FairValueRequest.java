package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class FairValueRequest {
    @NotNull @DecimalMin("0.0")
    private Double eps = 5000.0;

    @NotNull @DecimalMin("0.0")
    private Double targetPer = 12.0;

    @NotNull @DecimalMin("-100.0") @DecimalMax("300.0")
    private Double growthRate = 5.0;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double discountRate = 8.0;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double safetyMargin = 20.0;

    public Double getEps() { return eps; }
    public void setEps(Double eps) { this.eps = eps; }
    public Double getTargetPer() { return targetPer; }
    public void setTargetPer(Double targetPer) { this.targetPer = targetPer; }
    public Double getGrowthRate() { return growthRate; }
    public void setGrowthRate(Double growthRate) { this.growthRate = growthRate; }
    public Double getDiscountRate() { return discountRate; }
    public void setDiscountRate(Double discountRate) { this.discountRate = discountRate; }
    public Double getSafetyMargin() { return safetyMargin; }
    public void setSafetyMargin(Double safetyMargin) { this.safetyMargin = safetyMargin; }
}
