package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class LoanRefinanceRequest {
    @NotNull
    @Min(value = 1, message = "현재 대출 잔액은 1원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "현재 대출 잔액은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long currentBalance = 200_000_000L;

    @NotNull
    @DecimalMin(value = "0.0", message = "현재 대출 금리는 0% 이상 입력해주세요.")
    @DecimalMax(value = "100.0", message = "현재 대출 금리는 100% 이하로 입력해주세요.")
    private Double currentAnnualRate = 5.0;

    @NotNull
    @Min(value = 1, message = "현재 남은 기간은 1년 이상 입력해주세요.")
    @Max(value = 50, message = "현재 남은 기간은 50년 이하로 입력해주세요.")
    private Integer currentRemainingYears = 20;

    @NotNull
    @DecimalMin(value = "0.0", message = "신규 대출 금리는 0% 이상 입력해주세요.")
    @DecimalMax(value = "100.0", message = "신규 대출 금리는 100% 이하로 입력해주세요.")
    private Double newAnnualRate = 4.0;

    @NotNull
    @Min(value = 1, message = "신규 대출 기간은 1년 이상 입력해주세요.")
    @Max(value = 50, message = "신규 대출 기간은 50년 이하로 입력해주세요.")
    private Integer newYears = 20;

    @NotNull
    @DecimalMin(value = "0.0", message = "중도상환수수료율은 0% 이상 입력해주세요.")
    @DecimalMax(value = "100.0", message = "중도상환수수료율은 100% 이하로 입력해주세요.")
    private Double prepaymentPenaltyRate = 1.0;

    @NotNull
    @Min(value = 0, message = "인지세 및 기타 비용은 0원 이상 입력해주세요.")
    @Max(value = 999_999_999_999_999L, message = "인지세 및 기타 비용은 999,999,999,999,999원 이하로 입력해주세요.")
    private Long additionalCost = 500_000L;

    @NotNull
    @FutureOrPresent(message = "갈아타기 예상 실행일은 오늘 이후 날짜를 선택해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate refinanceDate = LocalDate.now();

    @NotNull
    @Pattern(regexp = "EQUAL_PAYMENT|EQUAL_PRINCIPAL", message = "유효한 상환방식을 선택해주세요.")
    private String repaymentType = "EQUAL_PAYMENT";

    public Long getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(Long currentBalance) { this.currentBalance = currentBalance; }
    public Double getCurrentAnnualRate() { return currentAnnualRate; }
    public void setCurrentAnnualRate(Double currentAnnualRate) { this.currentAnnualRate = currentAnnualRate; }
    public Integer getCurrentRemainingYears() { return currentRemainingYears; }
    public void setCurrentRemainingYears(Integer currentRemainingYears) { this.currentRemainingYears = currentRemainingYears; }
    public Double getNewAnnualRate() { return newAnnualRate; }
    public void setNewAnnualRate(Double newAnnualRate) { this.newAnnualRate = newAnnualRate; }
    public Integer getNewYears() { return newYears; }
    public void setNewYears(Integer newYears) { this.newYears = newYears; }
    public Double getPrepaymentPenaltyRate() { return prepaymentPenaltyRate; }
    public void setPrepaymentPenaltyRate(Double prepaymentPenaltyRate) { this.prepaymentPenaltyRate = prepaymentPenaltyRate; }
    public Long getAdditionalCost() { return additionalCost; }
    public void setAdditionalCost(Long additionalCost) { this.additionalCost = additionalCost; }
    public LocalDate getRefinanceDate() { return refinanceDate; }
    public void setRefinanceDate(LocalDate refinanceDate) { this.refinanceDate = refinanceDate; }
    public String getRepaymentType() { return repaymentType; }
    public void setRepaymentType(String repaymentType) { this.repaymentType = repaymentType; }
}
