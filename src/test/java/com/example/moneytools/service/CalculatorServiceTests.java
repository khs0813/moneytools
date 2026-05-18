package com.example.moneytools.service;

import com.example.moneytools.dto.DividendRequest;
import com.example.moneytools.dto.LoanRequest;
import com.example.moneytools.dto.SalaryRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorServiceTests {
    @Test
    void dividendCalculatorReturnsAnnualNet() {
        DividendRequest request = new DividendRequest();
        request.setShares(10L);
        request.setDividendPerShare(1000.0);
        request.setPeriod("QUARTERLY");
        request.setTaxApplied(true);
        request.setTaxRate(15.4);

        var result = new DividendCalculatorService().calculate(request);

        assertThat(result.paymentsPerYear()).isEqualTo(4);
        assertThat(result.annualNet()).isGreaterThan(0);
    }

    @Test
    void loanCalculatorReturnsSchedule() {
        LoanRequest request = new LoanRequest();
        request.setPrincipal(10_000_000L);
        request.setAnnualRate(4.0);
        request.setYears(1);
        request.setRepaymentType("EQUAL_PAYMENT");

        var result = new LoanCalculatorService().calculate(request);

        assertThat(result.totalMonths()).isEqualTo(12);
        assertThat(result.schedule()).hasSize(12);
        assertThat(result.totalPayment()).isGreaterThan(request.getPrincipal().doubleValue());
    }


    @Test
    void loanCalculatorSupportsLargePrincipal() {
        LoanRequest request = new LoanRequest();
        request.setPrincipal(20_000_000_000L);
        request.setAnnualRate(3.8);
        request.setYears(30);
        request.setRepaymentType("EQUAL_PAYMENT");

        var result = new LoanCalculatorService().calculate(request);

        assertThat(result.totalMonths()).isEqualTo(360);
        assertThat(result.schedule()).hasSize(360);
        assertThat(result.firstMonthlyPayment()).isGreaterThan(0.0);
        assertThat(result.totalPayment()).isGreaterThan(request.getPrincipal().doubleValue());
    }

    @Test
    void salaryCalculatorAppliesEligibleChildWithholdingCredit() {
        SalaryRequest withoutChildren = new SalaryRequest();
        withoutChildren.setAmount(8_000_000.0);
        withoutChildren.setTaxFreeAmount(0.0);
        withoutChildren.setDependents(4);
        withoutChildren.setChildren(0);

        SalaryRequest withTwoChildren = new SalaryRequest();
        withTwoChildren.setAmount(8_000_000.0);
        withTwoChildren.setTaxFreeAmount(0.0);
        withTwoChildren.setDependents(4);
        withTwoChildren.setChildren(2);

        SalaryCalculatorService service = new SalaryCalculatorService();

        var baseResult = service.calculate(withoutChildren);
        var childCreditResult = service.calculate(withTwoChildren);

        assertThat(baseResult.incomeTax() - childCreditResult.incomeTax()).isEqualTo(29_160.0);
        assertThat(baseResult.localIncomeTax()).isGreaterThan(childCreditResult.localIncomeTax());
    }
}
