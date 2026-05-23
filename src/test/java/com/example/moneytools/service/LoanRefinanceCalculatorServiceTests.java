package com.example.moneytools.service;

import com.example.moneytools.dto.LoanRefinanceRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoanRefinanceCalculatorServiceTests {
    private final LoanRefinanceCalculatorService service = new LoanRefinanceCalculatorService();

    @Test
    void recommendsRefinanceWhenSavingsArePositiveAndBreakEvenIsShort() {
        LoanRefinanceRequest request = new LoanRefinanceRequest();
        request.setCurrentBalance(200_000_000L);
        request.setCurrentAnnualRate(6.0);
        request.setCurrentRemainingYears(20);
        request.setNewAnnualRate(3.5);
        request.setNewYears(20);
        request.setPrepaymentPenaltyRate(0.5);
        request.setAdditionalCost(300_000L);
        request.setRepaymentType("EQUAL_PAYMENT");

        var result = service.calculate(request);

        assertThat(result.monthlySavings()).isGreaterThan(0.0);
        assertThat(result.netSavings()).isGreaterThan(0.0);
        assertThat(result.recommendation()).isIn("갈아타기 유리", "장기 보유 시 유리");
    }

    @Test
    void marksRefinanceUnfavorableWhenNewRateIsHigher() {
        LoanRefinanceRequest request = new LoanRefinanceRequest();
        request.setCurrentBalance(100_000_000L);
        request.setCurrentAnnualRate(3.0);
        request.setCurrentRemainingYears(10);
        request.setNewAnnualRate(5.0);
        request.setNewYears(10);
        request.setPrepaymentPenaltyRate(1.0);
        request.setAdditionalCost(500_000L);
        request.setRepaymentType("EQUAL_PRINCIPAL");

        var result = service.calculate(request);

        assertThat(result.netSavings()).isLessThanOrEqualTo(0.0);
        assertThat(result.recommendation()).isEqualTo("갈아타기 불리");
        assertThat(result.recommendationLevel()).isEqualTo("danger");
    }
}
