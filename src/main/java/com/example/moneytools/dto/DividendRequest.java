package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DividendRequest {
    @NotNull @Min(0) @Max(999_999_999L)
    private Long shares = 100L;

    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double dividendPerShare = 500.0;

    @NotNull
    @Pattern(regexp = "MONTHLY|QUARTERLY|SEMI_ANNUAL|ANNUAL", message = "유효한 배당 주기를 선택해주세요.")
    private String period = "QUARTERLY";

    private boolean taxApplied = true;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double taxRate = 15.4;

    public Long getShares() { return shares; }
    public void setShares(Long shares) { this.shares = shares; }
    public Double getDividendPerShare() { return dividendPerShare; }
    public void setDividendPerShare(Double dividendPerShare) { this.dividendPerShare = dividendPerShare; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public boolean isTaxApplied() { return taxApplied; }
    public void setTaxApplied(boolean taxApplied) { this.taxApplied = taxApplied; }
    public Double getTaxRate() { return taxRate; }
    public void setTaxRate(Double taxRate) { this.taxRate = taxRate; }
}
