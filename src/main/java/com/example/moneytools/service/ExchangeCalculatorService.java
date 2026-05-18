package com.example.moneytools.service;

import com.example.moneytools.dto.ExchangeRequest;
import com.example.moneytools.dto.ExchangeResult;
import org.springframework.stereotype.Service;

@Service
public class ExchangeCalculatorService {
    public ExchangeResult calculate(ExchangeRequest request) {
        double beforeFee = request.getAmount() * request.getExchangeRate();
        double fee = beforeFee * request.getFeeRate() / 100.0;
        double afterFee = Math.max(0, beforeFee - fee);
        return new ExchangeResult(beforeFee, fee, afterFee);
    }
}
