package com.example.moneytools.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidationTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsOversizedSalaryAmount() {
        SalaryRequest request = new SalaryRequest();
        request.setAmount(1.0e18);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsOversizedExchangeRate() {
        ExchangeRequest request = new ExchangeRequest();
        request.setExchangeRate(1.0e18);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsTooManyUsedLeaveDays() {
        AnnualLeaveRequest request = new AnnualLeaveRequest();
        request.setUsedLeaveDays(36501);

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
