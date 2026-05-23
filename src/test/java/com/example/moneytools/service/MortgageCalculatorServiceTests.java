package com.example.moneytools.service;

import com.example.moneytools.dto.MortgageRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MortgageCalculatorServiceTests {
    private final MortgageCalculatorService service = new MortgageCalculatorService();

    @Test
    void equalPaymentMortgageReturnsMonthlyPaymentAndRisk() {
        MortgageRequest request = new MortgageRequest();
        request.setHousePrice(600_000_000L);
        request.setCashOnHand(200_000_000L);
        request.setExpectedLoanAmount(400_000_000L);
        request.setAnnualRate(4.0);
        request.setYears(30);
        request.setRepaymentType("EQUAL_PAYMENT");
        request.setLtvRatio(70.0);
        request.setAnnualIncome(80_000_000L);
        request.setExistingMonthlyDebtPayment(0L);

        var result = service.calculate(request);

        assertThat(result.estimatedMonthlyPayment()).isGreaterThan(0.0);
        assertThat(result.totalInterest()).isGreaterThan(0.0);
        assertThat(result.totalPayment()).isGreaterThan(request.getExpectedLoanAmount().doubleValue());
        assertThat(result.requiredEquity()).isEqualTo(200_000_000.0);
        assertThat(result.maxLoanByLtv()).isEqualTo(420_000_000.0);
        assertThat(result.riskLabel()).isIn("안전", "주의", "위험");
    }

    @Test
    void bulletMortgageUsesMonthlyInterestAsMonthlyPayment() {
        MortgageRequest request = new MortgageRequest();
        request.setExpectedLoanAmount(300_000_000L);
        request.setAnnualRate(6.0);
        request.setYears(10);
        request.setRepaymentType("BULLET");

        var result = service.calculate(request);

        assertThat(result.estimatedMonthlyPayment()).isEqualTo(1_500_000.0);
        assertThat(result.totalInterest()).isEqualTo(180_000_000.0);
    }
}
