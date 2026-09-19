package com.example.moneytools.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeRateTests {
    @Test
    void calculatesRateUsingPreviousValueAsDenominator() {
        assertThat(ChangeRate.calculate(24, 17).percent()).isEqualTo(-29.17);
        assertThat(ChangeRate.calculate(71.64, 29.51).percent()).isEqualTo(-58.81);
        assertThat(ChangeRate.calculate(10, 8).percent()).isEqualTo(-20.0);
        assertThat(ChangeRate.calculate(80, 37).percent()).isEqualTo(-53.75);
    }

    @Test
    void handlesZeroAndInvalidPreviousValuesWithoutExposingNanOrInfinity() {
        assertThat(ChangeRate.calculate(0, 10).status()).isEqualTo(ChangeRate.Status.NEW);
        assertThat(ChangeRate.calculate(0, 0).percent()).isEqualTo(0.0);
        assertThat(ChangeRate.calculate(10, 0).percent()).isEqualTo(-100.0);
        assertThat(ChangeRate.calculate(Double.NaN, 10).status()).isEqualTo(ChangeRate.Status.NOT_COMPARABLE);
        assertThat(ChangeRate.calculate(10, Double.POSITIVE_INFINITY).status()).isEqualTo(ChangeRate.Status.NOT_COMPARABLE);
    }
}
