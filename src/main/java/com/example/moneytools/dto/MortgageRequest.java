package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class MortgageRequest {
    @NotNull
    @Min(value = 1, message = "주택 가격은 1원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "주택 가격은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long housePrice = 600_000_000L;

    @NotNull
    @Min(value = 0, message = "보유 현금은 0원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "보유 현금은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long cashOnHand = 200_000_000L;

    @NotNull
    @Min(value = 1, message = "예상 대출금액은 1원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "예상 대출금액은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long expectedLoanAmount = 400_000_000L;

    @NotNull
    @DecimalMin(value = "0.0", message = "연 이자율은 0% 이상 입력해주세요.")
    @DecimalMax(value = "100.0", message = "연 이자율은 100% 이하로 입력해주세요.")
    private Double annualRate = 4.0;

    @NotNull
    @Min(value = 1, message = "대출 기간은 1년 이상 입력해주세요.")
    @Max(value = 50, message = "대출 기간은 50년 이하로 입력해주세요.")
    private Integer years = 30;

    @NotNull
    @Pattern(regexp = "EQUAL_PAYMENT|EQUAL_PRINCIPAL|BULLET", message = "유효한 상환방식을 선택해주세요.")
    private String repaymentType = "EQUAL_PAYMENT";

    @NotNull
    @DecimalMin(value = "0.0", message = "LTV 비율은 0% 이상 입력해주세요.")
    @DecimalMax(value = "100.0", message = "LTV 비율은 100% 이하로 입력해주세요.")
    private Double ltvRatio = 70.0;

    @NotNull
    @Min(value = 1, message = "연소득은 1원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "연소득은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long annualIncome = 80_000_000L;

    @NotNull
    @Min(value = 0, message = "기존 대출 월 상환액은 0원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "기존 대출 월 상환액은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long existingMonthlyDebtPayment = 0L;

    public Long getHousePrice() { return housePrice; }
    public void setHousePrice(Long housePrice) { this.housePrice = housePrice; }
    public Long getCashOnHand() { return cashOnHand; }
    public void setCashOnHand(Long cashOnHand) { this.cashOnHand = cashOnHand; }
    public Long getExpectedLoanAmount() { return expectedLoanAmount; }
    public void setExpectedLoanAmount(Long expectedLoanAmount) { this.expectedLoanAmount = expectedLoanAmount; }
    public Double getAnnualRate() { return annualRate; }
    public void setAnnualRate(Double annualRate) { this.annualRate = annualRate; }
    public Integer getYears() { return years; }
    public void setYears(Integer years) { this.years = years; }
    public String getRepaymentType() { return repaymentType; }
    public void setRepaymentType(String repaymentType) { this.repaymentType = repaymentType; }
    public Double getLtvRatio() { return ltvRatio; }
    public void setLtvRatio(Double ltvRatio) { this.ltvRatio = ltvRatio; }
    public Long getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(Long annualIncome) { this.annualIncome = annualIncome; }
    public Long getExistingMonthlyDebtPayment() { return existingMonthlyDebtPayment; }
    public void setExistingMonthlyDebtPayment(Long existingMonthlyDebtPayment) { this.existingMonthlyDebtPayment = existingMonthlyDebtPayment; }
}
