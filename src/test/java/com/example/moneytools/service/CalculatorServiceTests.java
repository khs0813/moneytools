package com.example.moneytools.service;

import com.example.moneytools.dto.DividendRequest;
import com.example.moneytools.dto.AirConditionerCostRequest;
import com.example.moneytools.dto.ExchangeRequest;
import com.example.moneytools.dto.LoanRequest;
import com.example.moneytools.dto.SalaryRequest;
import com.example.moneytools.dto.StockAverageRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.offset;

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
        assertThat(result.lastMonthlyPayment()).isGreaterThan(0.0);
        assertThat(result.totalPayment()).isGreaterThan(request.getPrincipal().doubleValue());
    }

    @Test
    void stockAverageCalculatorReturnsNewAveragePrice() {
        StockAverageRequest request = new StockAverageRequest();
        request.setCurrentShares(100L);
        request.setCurrentAveragePrice(50_000.0);
        request.setAdditionalShares(50L);
        request.setAdditionalPrice(40_000.0);

        var result = new StockAverageCalculatorService().calculate(request);

        assertThat(result.totalShares()).isEqualTo(150L);
        assertThat(result.totalInvestment()).isEqualTo(7_000_000.0);
        assertThat(result.newAveragePrice()).isBetween(46_666.66, 46_666.67);
        assertThat(result.averagePriceChange()).isLessThan(0.0);
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

        assertThat(baseResult.incomeTax() - childCreditResult.incomeTax()).isEqualTo(45_830.0);
        assertThat(baseResult.localIncomeTax()).isGreaterThan(childCreditResult.localIncomeTax());
    }

    @Test
    void exchangeCalculatorConvertsForeignToKrwUsingKrwPerForeignRate() {
        ExchangeRequest request = new ExchangeRequest();
        request.setAmount(1_000.0);
        request.setFromCurrency("USD");
        request.setToCurrency("KRW");
        request.setExchangeRate(1_350.0);
        request.setFeeRate(0.0);

        var result = new ExchangeCalculatorService().calculate(request);

        assertThat(result.beforeFeeAmount()).isEqualTo(1_350_000.0);
        assertThat(result.feeAmount()).isEqualTo(0.0);
        assertThat(result.afterFeeAmount()).isEqualTo(1_350_000.0);
        assertThat(result.afterFeeKrw()).isEqualTo(1_350_000.0);
    }

    @Test
    void exchangeCalculatorConvertsKrwToForeignByDividingByKrwPerForeignRate() {
        ExchangeRequest request = new ExchangeRequest();
        request.setAmount(1_350_000.0);
        request.setFromCurrency("KRW");
        request.setToCurrency("USD");
        request.setExchangeRate(1_350.0);
        request.setFeeRate(0.0);

        var result = new ExchangeCalculatorService().calculate(request);

        assertThat(result.beforeFeeAmount()).isEqualTo(1_000.0);
        assertThat(result.afterFeeAmount()).isEqualTo(1_000.0);
        assertThat(result.afterFeeKrw()).isEqualTo(1_350_000.0);
    }

    @Test
    void exchangeCalculatorKeepsSameCurrencyAsNoOp() {
        ExchangeRequest request = new ExchangeRequest();
        request.setAmount(1_000.0);
        request.setFromCurrency("USD");
        request.setToCurrency("USD");
        request.setExchangeRate(1_350.0);
        request.setFeeRate(3.0);

        var result = new ExchangeCalculatorService().calculate(request);

        assertThat(result.sameCurrency()).isTrue();
        assertThat(result.beforeFeeAmount()).isEqualTo(1_000.0);
        assertThat(result.feeAmount()).isEqualTo(0.0);
        assertThat(result.afterFeeAmount()).isEqualTo(1_000.0);
    }

    @Test
    void exchangeCalculatorRejectsZeroOrNegativeRate() {
        ExchangeRequest request = new ExchangeRequest();
        request.setExchangeRate(0.0);

        assertThatThrownBy(() -> new ExchangeCalculatorService().calculate(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void airConditionerCalculatorSeparatesStandaloneAndHouseholdIncrement() {
        AirConditionerCostRequest request = new AirConditionerCostRequest();
        request.setPowerWatts(1800.0);
        request.setHoursPerDay(8.0);
        request.setLoadFactor(1.0);
        request.setDaysPerMonth(30.0);
        request.setElectricityRatePerKwh(160.0);
        request.setStandbyWatts(7.5);
        request.setHouseholdUsageKwh(250.0);
        request.setSeason("SUMMER");

        var result = new AirConditionerCostCalculatorService(new ElectricityBillCalculatorService()).calculate(request);

        assertThat(result.estimatedCost()).isGreaterThan(0.0);
        assertThat(result.householdTotalUsageKwh()).isGreaterThan(result.householdBaseUsageKwh());
        assertThat(result.householdIncrementalCost()).isGreaterThan(0.0);
    }

    @Test
    void airConditionerCalculatorUsesStandbyOnlyOutsideActiveHours() {
        AirConditionerCostCalculatorService service = new AirConditionerCostCalculatorService(new ElectricityBillCalculatorService());

        assertThat(airConditionerUsage(service, 4.0)).isCloseTo(220.5, offset(0.0001));
        assertThat(airConditionerUsage(service, 8.0)).isCloseTo(435.6, offset(0.0001));
        assertThat(airConditionerUsage(service, 12.0)).isCloseTo(650.7, offset(0.0001));
        assertThat(airConditionerUsage(service, 24.0)).isCloseTo(1296.0, offset(0.0001));
    }

    @Test
    void electricityBillCalculatorUses2026IndustryFundRateAndTenWonFloor() {
        var result = new ElectricityBillCalculatorService().calculate(new com.example.moneytools.dto.ElectricityBillRequest());

        assertThat(result.industryFund()).isEqualTo(1_690.0);
        assertThat(result.totalBill()).isEqualTo(70_640.0);
    }

    private double airConditionerUsage(AirConditionerCostCalculatorService service, double hoursPerDay) {
        AirConditionerCostRequest request = new AirConditionerCostRequest();
        request.setPowerWatts(1800.0);
        request.setStandbyWatts(7.5);
        request.setDaysPerMonth(30.0);
        request.setHoursPerDay(hoursPerDay);
        request.setLoadFactor(1.0);
        request.setElectricityRatePerKwh(160.0);
        request.setHouseholdUsageKwh(250.0);
        request.setSeason("SUMMER");
        return service.calculate(request).totalUsageKwh();
    }
}
