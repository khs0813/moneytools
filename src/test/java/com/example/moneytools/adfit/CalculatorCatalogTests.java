package com.example.moneytools.adfit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorCatalogTests {
    @Test
    void allCalculatorRoutesAreExplicitlyClassified() {
        assertThat(CalculatorCatalog.unclassifiedCalculatorKeys()).isEmpty();
        assertThat(CalculatorCatalog.ALL).hasSize(CalculatorCatalog.calculatorKeys().size());
    }

    @Test
    void groupsCoreCalculatorsForAdfitMeasurement() {
        assertThat(CalculatorCatalog.require("loan").group()).isEqualTo(MoneyCalculatorGroup.FINANCE);
        assertThat(CalculatorCatalog.require("salary").group()).isEqualTo(MoneyCalculatorGroup.INCOME);
        assertThat(CalculatorCatalog.require("monthly-budget").group()).isEqualTo(MoneyCalculatorGroup.LIVING);
    }
}
