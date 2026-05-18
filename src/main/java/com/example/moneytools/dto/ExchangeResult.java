package com.example.moneytools.dto;

public record ExchangeResult(
        double beforeFeeAmount,
        double feeAmount,
        double afterFeeAmount
) {
}
