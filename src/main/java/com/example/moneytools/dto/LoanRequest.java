package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class LoanRequest {
    @NotNull
    @Min(value = 1, message = "대출원금은 1원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "대출원금은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long principal = 100_000_000L;

    @NotNull
    @DecimalMin(value = "0.0", message = "연이율은 0% 이상 입력해주세요.")
    @DecimalMax(value = "100.0", message = "연이율은 100% 이하로 입력해주세요.")
    private Double annualRate = 4.5;

    @NotNull
    @Min(value = 1, message = "대출기간은 1년 이상 입력해주세요.")
    @Max(value = 50, message = "대출기간은 50년 이하로 입력해주세요.")
    private Integer years = 20;

    @NotNull
    @Pattern(regexp = "EQUAL_PAYMENT|EQUAL_PRINCIPAL|BULLET", message = "유효한 상환방식을 선택해주세요.")
    private String repaymentType = "EQUAL_PAYMENT";

    public Long getPrincipal() { return principal; }
    public void setPrincipal(Long principal) { this.principal = principal; }
    public Double getAnnualRate() { return annualRate; }
    public void setAnnualRate(Double annualRate) { this.annualRate = annualRate; }
    public Integer getYears() { return years; }
    public void setYears(Integer years) { this.years = years; }
    public String getRepaymentType() { return repaymentType; }
    public void setRepaymentType(String repaymentType) { this.repaymentType = repaymentType; }
}
