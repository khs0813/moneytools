package com.example.moneytools.dto;

import com.example.moneytools.unit.CurrencyCode;
import com.example.moneytools.unit.ExchangeQuoteType;

public record ExchangeResult(
        double beforeFeeAmount,
        double feeAmount,
        double afterFeeAmount,
        CurrencyCode amountCurrency,
        CurrencyCode feeCurrency,
        double beforeFeeKrw,
        double feeKrw,
        double afterFeeKrw,
        ExchangeQuoteType quoteType,
        boolean sameCurrency
) {
}
