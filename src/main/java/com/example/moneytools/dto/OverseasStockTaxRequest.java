package com.example.moneytools.dto;

import com.example.moneytools.policy.Policy2026;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class OverseasStockTaxRequest {
    @NotNull @DecimalMin("0.0")
    private Double buyAmountForeign = 10_000.0;

    @NotNull @DecimalMin("0.0")
    private Double sellAmountForeign = 13_000.0;

    @NotNull @DecimalMin("0.0")
    private Double dividendForeign = 300.0;

    @NotNull @DecimalMin("0.0")
    private Double buyExchangeRate = 1_350.0;

    @NotNull @DecimalMin("0.0")
    private Double sellExchangeRate = 1_350.0;

    @NotNull @DecimalMin("0.0")
    private Double dividendExchangeRate = 1_350.0;

    @NotNull @DecimalMin("0.0")
    private Double feeKrw = 50_000.0;

    private boolean applyBasicDeduction = true;

    @NotNull @DecimalMin("0.0")
    private Double basicDeductionKrw = Policy2026.OVERSEAS_STOCK_BASIC_DEDUCTION_KRW;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double capitalGainsTaxRate = Policy2026.OVERSEAS_STOCK_DEFAULT_CAPITAL_GAINS_TAX_RATE;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double dividendTaxRate = Policy2026.OVERSEAS_STOCK_DEFAULT_DIVIDEND_TAX_RATE;

    public Double getBuyAmountForeign() { return buyAmountForeign; }
    public void setBuyAmountForeign(Double buyAmountForeign) { this.buyAmountForeign = buyAmountForeign; }
    public Double getSellAmountForeign() { return sellAmountForeign; }
    public void setSellAmountForeign(Double sellAmountForeign) { this.sellAmountForeign = sellAmountForeign; }
    public Double getDividendForeign() { return dividendForeign; }
    public void setDividendForeign(Double dividendForeign) { this.dividendForeign = dividendForeign; }
    public Double getBuyExchangeRate() { return buyExchangeRate; }
    public void setBuyExchangeRate(Double buyExchangeRate) { this.buyExchangeRate = buyExchangeRate; }
    public Double getSellExchangeRate() { return sellExchangeRate; }
    public void setSellExchangeRate(Double sellExchangeRate) { this.sellExchangeRate = sellExchangeRate; }
    public Double getDividendExchangeRate() { return dividendExchangeRate; }
    public void setDividendExchangeRate(Double dividendExchangeRate) { this.dividendExchangeRate = dividendExchangeRate; }
    public Double getFeeKrw() { return feeKrw; }
    public void setFeeKrw(Double feeKrw) { this.feeKrw = feeKrw; }
    public boolean isApplyBasicDeduction() { return applyBasicDeduction; }
    public void setApplyBasicDeduction(boolean applyBasicDeduction) { this.applyBasicDeduction = applyBasicDeduction; }
    public Double getBasicDeductionKrw() { return basicDeductionKrw; }
    public void setBasicDeductionKrw(Double basicDeductionKrw) { this.basicDeductionKrw = basicDeductionKrw; }
    public Double getCapitalGainsTaxRate() { return capitalGainsTaxRate; }
    public void setCapitalGainsTaxRate(Double capitalGainsTaxRate) { this.capitalGainsTaxRate = capitalGainsTaxRate; }
    public Double getDividendTaxRate() { return dividendTaxRate; }
    public void setDividendTaxRate(Double dividendTaxRate) { this.dividendTaxRate = dividendTaxRate; }
}
