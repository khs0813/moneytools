package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SalaryRequest {
    @NotNull
    @Pattern(regexp = "MONTHLY|ANNUAL", message = "유효한 입력 기준을 선택해주세요.")
    private String incomeType = "MONTHLY";

    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double amount = 3500000.0;

    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double taxFreeAmount = 200000.0;

    @NotNull @Min(1) @Max(20)
    private Integer dependents = 1;

    @NotNull @Min(0) @Max(19)
    private Integer children = 0;

    private boolean applyInsurance = true;

    public String getIncomeType() { return incomeType; }
    public void setIncomeType(String incomeType) { this.incomeType = incomeType; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getTaxFreeAmount() { return taxFreeAmount; }
    public void setTaxFreeAmount(Double taxFreeAmount) { this.taxFreeAmount = taxFreeAmount; }
    public Integer getDependents() { return dependents; }
    public void setDependents(Integer dependents) { this.dependents = dependents; }
    public Integer getChildren() { return children; }
    public void setChildren(Integer children) { this.children = children; }
    public boolean isApplyInsurance() { return applyInsurance; }
    public void setApplyInsurance(boolean applyInsurance) { this.applyInsurance = applyInsurance; }
}
