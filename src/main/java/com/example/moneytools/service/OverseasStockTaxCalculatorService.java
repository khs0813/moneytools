package com.example.moneytools.service;

import com.example.moneytools.dto.OverseasStockTaxRequest;
import com.example.moneytools.dto.OverseasStockTaxResult;
import org.springframework.stereotype.Service;

@Service
public class OverseasStockTaxCalculatorService {
    public OverseasStockTaxResult calculate(OverseasStockTaxRequest request) {
        double buyKrw = request.getBuyAmountForeign() * request.getBuyExchangeRate();
        double sellKrw = request.getSellAmountForeign() * request.getSellExchangeRate();
        double capitalGain = sellKrw - buyKrw - request.getFeeKrw();

        double deduction = request.isApplyBasicDeduction() ? request.getBasicDeductionKrw() : 0.0;
        double taxableCapitalGain = Math.max(0, capitalGain - deduction);
        double capitalTax = taxableCapitalGain * request.getCapitalGainsTaxRate() / 100.0;

        double dividendKrw = request.getDividendForeign() * request.getDividendExchangeRate();
        double dividendTax = dividendKrw * request.getDividendTaxRate() / 100.0;

        double totalTax = capitalTax + dividendTax;
        double afterTaxProfit = capitalGain + dividendKrw - totalTax;

        return new OverseasStockTaxResult(
                buyKrw,
                sellKrw,
                capitalGain,
                taxableCapitalGain,
                capitalTax,
                dividendKrw,
                dividendTax,
                totalTax,
                afterTaxProfit
        );
    }
}
