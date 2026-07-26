package com.example.moneytools.controller;

import com.example.moneytools.config.AppProperties;
import com.example.moneytools.dto.SalaryRequest;
import com.example.moneytools.seo.PublicUrlService;
import com.example.moneytools.seo.SeoService;
import com.example.moneytools.service.AirConditionerCostCalculatorService;
import com.example.moneytools.service.AnnualLeaveCalculatorService;
import com.example.moneytools.service.CarMaintenanceCalculatorService;
import com.example.moneytools.service.DividendCalculatorService;
import com.example.moneytools.service.ElectricityBillCalculatorService;
import com.example.moneytools.service.ExchangeCalculatorService;
import com.example.moneytools.service.FairValueCalculatorService;
import com.example.moneytools.service.LoanCalculatorService;
import com.example.moneytools.service.LoanRefinanceCalculatorService;
import com.example.moneytools.service.MonthlyBudgetCalculatorService;
import com.example.moneytools.service.MortgageCalculatorService;
import com.example.moneytools.service.OverseasStockTaxCalculatorService;
import com.example.moneytools.service.SalaryCalculatorService;
import com.example.moneytools.service.SeveranceCalculatorService;
import com.example.moneytools.service.StockAverageCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorControllerTests {
    private final SalaryCalculatorService salaryService = new SalaryCalculatorService();

    @Test
    void annualSalaryExamplesUseSalaryCalculatorResultValues() {
        CalculatorController controller = calculatorController();
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.annualSalaryNet(model);

        assertThat(viewName).isEqualTo("annual-salary-net-calculator");
        @SuppressWarnings("unchecked")
        List<CalculatorController.SalaryExampleRow> rows =
                (List<CalculatorController.SalaryExampleRow>) model.getAttribute("annualSalaryExamples");
        assertThat(rows)
                .extracting(CalculatorController.SalaryExampleRow::annualSalary)
                .containsExactly("3,000만원", "4,000만원", "5,000만원", "6,000만원", "7,000만원", "8,000만원", "1억원");

        CalculatorController.SalaryExampleRow fiftyMillionRow = rows.get(2);
        var expected = salaryService.calculate(annualSalaryRequest(50_000_000L));

        assertThat(fiftyMillionRow.grossMonthly()).isEqualTo(expected.grossMonthly());
        assertThat(fiftyMillionRow.insuranceDeduction()).isEqualTo(
                expected.nationalPension()
                        + expected.healthInsurance()
                        + expected.longTermCareInsurance()
                        + expected.employmentInsurance());
        assertThat(fiftyMillionRow.taxDeduction()).isEqualTo(expected.incomeTax() + expected.localIncomeTax());
        assertThat(fiftyMillionRow.netMonthly()).isEqualTo(expected.netMonthly());
        assertThat(fiftyMillionRow.netAnnual()).isEqualTo(expected.netAnnual());
    }

    private SalaryRequest annualSalaryRequest(long annualSalary) {
        SalaryRequest request = new SalaryRequest();
        request.setIncomeType("ANNUAL");
        request.setAmount((double) annualSalary);
        request.setTaxFreeAmount(200_000.0);
        request.setDependents(1);
        request.setChildren(0);
        request.setApplyInsurance(true);
        return request;
    }

    private CalculatorController calculatorController() {
        AppProperties appProperties = new AppProperties();
        PublicUrlService publicUrlService = new PublicUrlService(appProperties);
        SeoService seoService = new SeoService(appProperties, new ObjectMapper(), publicUrlService);
        ElectricityBillCalculatorService electricityBillService = new ElectricityBillCalculatorService();
        AirConditionerCostCalculatorService airConditionerCostService = new AirConditionerCostCalculatorService(electricityBillService);
        return new CalculatorController(
                seoService,
                publicUrlService,
                new DividendCalculatorService(),
                new FairValueCalculatorService(),
                new LoanCalculatorService(),
                new LoanRefinanceCalculatorService(),
                new MortgageCalculatorService(),
                salaryService,
                new SeveranceCalculatorService(),
                new AnnualLeaveCalculatorService(),
                new ExchangeCalculatorService(),
                new OverseasStockTaxCalculatorService(),
                new StockAverageCalculatorService(),
                electricityBillService,
                airConditionerCostService,
                new CarMaintenanceCalculatorService(),
                new MonthlyBudgetCalculatorService()
        );
    }
}
