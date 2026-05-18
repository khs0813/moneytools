package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ExchangeRequest {
    @NotNull @DecimalMin("0.0")
    private Double amount = 1000.0;

    @NotNull
    @Pattern(regexp = "USD|KRW|JPY|EUR|CNY", message = "유효한 보내는 통화를 선택해주세요.")
    private String fromCurrency = "USD";

    @NotNull
    @Pattern(regexp = "USD|KRW|JPY|EUR|CNY", message = "유효한 받는 통화를 선택해주세요.")
    private String toCurrency = "KRW";

    @NotNull @DecimalMin("0.0")
    private Double exchangeRate = 1350.0;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double feeRate = 1.0;

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public Double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(Double exchangeRate) { this.exchangeRate = exchangeRate; }
    public Double getFeeRate() { return feeRate; }
    public void setFeeRate(Double feeRate) { this.feeRate = feeRate; }
}
