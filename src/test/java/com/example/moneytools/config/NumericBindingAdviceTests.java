package com.example.moneytools.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.WebDataBinder;

import static org.assertj.core.api.Assertions.assertThat;

class NumericBindingAdviceTests {
    @Test
    void rejectsOverlyLongNumericTextBeforeConversion() {
        WebDataBinder binder = new WebDataBinder(new SampleForm());
        new NumericBindingAdvice().initBinder(binder);
        binder.bind(new org.springframework.beans.MutablePropertyValues()
                .add("amount", "1e999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999"));

        assertThat(binder.getBindingResult().hasFieldErrors("amount")).isTrue();
    }

    @Test
    void rejectsShortScientificNotationThatWouldExpandTooLarge() {
        WebDataBinder binder = new WebDataBinder(new SampleForm());
        new NumericBindingAdvice().initBinder(binder);
        binder.bind(new org.springframework.beans.MutablePropertyValues()
                .add("amount", "1e999999999"));

        assertThat(binder.getBindingResult().hasFieldErrors("amount")).isTrue();
    }

    @Test
    void rejectsNonFiniteDoubleValues() {
        WebDataBinder binder = new WebDataBinder(new SampleForm());
        new NumericBindingAdvice().initBinder(binder);
        binder.bind(new org.springframework.beans.MutablePropertyValues()
                .add("amount", "Infinity"));

        assertThat(binder.getBindingResult().hasFieldErrors("amount")).isTrue();
    }

    static class SampleForm {
        private Double amount;

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}
