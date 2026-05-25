package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockAverageRequest {
    @NotNull
    @Min(value = 0, message = "보유 수량은 0주 이상 입력해주세요.")
    @Max(value = 999_999_999L, message = "보유 수량은 999,999,999주 이하로 입력해주세요.")
    private Long currentShares = 100L;

    @NotNull
    @DecimalMin(value = "0.0", message = "현재 평균단가는 0원 이상 입력해주세요.")
    @DecimalMax(value = "999999999999999.0", message = "현재 평균단가는 999,999,999,999,999원 이하로 입력해주세요.")
    private Double currentAveragePrice = 50_000.0;

    @NotNull
    @Min(value = 1, message = "추가 매수 수량은 1주 이상 입력해주세요.")
    @Max(value = 999_999_999L, message = "추가 매수 수량은 999,999,999주 이하로 입력해주세요.")
    private Long additionalShares = 50L;

    @NotNull
    @DecimalMin(value = "0.0", message = "추가 매수 단가는 0원 이상 입력해주세요.")
    @DecimalMax(value = "999999999999999.0", message = "추가 매수 단가는 999,999,999,999,999원 이하로 입력해주세요.")
    private Double additionalPrice = 40_000.0;

    public Long getCurrentShares() { return currentShares; }
    public void setCurrentShares(Long currentShares) { this.currentShares = currentShares; }
    public Double getCurrentAveragePrice() { return currentAveragePrice; }
    public void setCurrentAveragePrice(Double currentAveragePrice) { this.currentAveragePrice = currentAveragePrice; }
    public Long getAdditionalShares() { return additionalShares; }
    public void setAdditionalShares(Long additionalShares) { this.additionalShares = additionalShares; }
    public Double getAdditionalPrice() { return additionalPrice; }
    public void setAdditionalPrice(Double additionalPrice) { this.additionalPrice = additionalPrice; }
}
