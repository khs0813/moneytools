package com.example.moneytools.service;

import com.example.moneytools.dto.ExchangeRequest;
import com.example.moneytools.dto.ExchangeResult;
import com.example.moneytools.unit.CurrencyCode;
import com.example.moneytools.unit.ExchangeQuoteType;
import com.example.moneytools.unit.Money;
import com.example.moneytools.unit.Percentage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class ExchangeCalculatorService {
    public ExchangeResult calculate(ExchangeRequest request) {
        CurrencyCode from = CurrencyCode.valueOf(request.getFromCurrency());
        CurrencyCode to = CurrencyCode.valueOf(request.getToCurrency());
        Money source = new Money(BigDecimal.valueOf(request.getAmount()), from);
        BigDecimal primaryKrwPerForeign = positiveRate(request.getExchangeRate(), "환율");
        BigDecimal targetKrwPerForeign = positiveRate(request.getTargetExchangeRate(), "받는 통화 환율");

        if (from == to) {
            BigDecimal sameCurrencyKrw = krwEquivalent(source.amount(), from, primaryKrwPerForeign);
            return new ExchangeResult(
                    source.amount().doubleValue(),
                    0.0,
                    source.amount().doubleValue(),
                    to,
                    to,
                    sameCurrencyKrw.doubleValue(),
                    0.0,
                    sameCurrencyKrw.doubleValue(),
                    ExchangeQuoteType.KRW_PER_FOREIGN,
                    true
            );
        }

        Conversion conversion = convert(source, to, primaryKrwPerForeign, targetKrwPerForeign);
        BigDecimal fee = conversion.targetMoney().amount()
                .multiply(Percentage.ofPercent(request.getFeeRate()).fraction(), MathContext.DECIMAL64);
        BigDecimal afterFee = conversion.targetMoney().amount().subtract(fee).max(BigDecimal.ZERO);
        BigDecimal feeKrw = krwEquivalent(fee, to, conversion.krwPerTargetCurrency());
        BigDecimal afterFeeKrw = krwEquivalent(afterFee, to, conversion.krwPerTargetCurrency());

        return new ExchangeResult(
                conversion.targetMoney().amount().doubleValue(),
                fee.doubleValue(),
                afterFee.doubleValue(),
                to,
                to,
                conversion.beforeFeeKrw().doubleValue(),
                feeKrw.doubleValue(),
                afterFeeKrw.doubleValue(),
                ExchangeQuoteType.KRW_PER_FOREIGN,
                false
        );
    }

    private Conversion convert(Money source, CurrencyCode target, BigDecimal primaryKrwPerForeign, BigDecimal targetKrwPerForeign) {
        CurrencyCode from = source.currency();
        if (target.isKrw()) {
            BigDecimal krw = source.amount().multiply(primaryKrwPerForeign, MathContext.DECIMAL64);
            return new Conversion(new Money(krw, target), krw, BigDecimal.ONE);
        }

        if (from.isKrw()) {
            BigDecimal targetAmount = source.amount().divide(primaryKrwPerForeign, MathContext.DECIMAL64);
            return new Conversion(new Money(targetAmount, target), source.amount(), primaryKrwPerForeign);
        }

        BigDecimal sourceKrw = source.amount().multiply(primaryKrwPerForeign, MathContext.DECIMAL64);
        BigDecimal targetAmount = sourceKrw.divide(targetKrwPerForeign, MathContext.DECIMAL64);
        return new Conversion(new Money(targetAmount, target), sourceKrw, targetKrwPerForeign);
    }

    private BigDecimal krwEquivalent(BigDecimal amount, CurrencyCode currency, BigDecimal krwPerForeign) {
        return currency.isKrw() ? amount : amount.multiply(krwPerForeign, MathContext.DECIMAL64);
    }

    private BigDecimal positiveRate(Double value, String fieldName) {
        BigDecimal rate = BigDecimal.valueOf(value == null ? 0.0 : value);
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + "은 0보다 커야 합니다.");
        }
        return rate;
    }

    private record Conversion(Money targetMoney, BigDecimal beforeFeeKrw, BigDecimal krwPerTargetCurrency) {
    }
}
