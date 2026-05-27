package com.example.moneytools.controller;

import com.example.moneytools.dto.*;
import com.example.moneytools.seo.FaqItem;
import com.example.moneytools.seo.PageInfo;
import com.example.moneytools.seo.PublicUrlService;
import com.example.moneytools.seo.SeoService;
import com.example.moneytools.seo.SitePages;
import com.example.moneytools.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class CalculatorController {
    private final SeoService seoService;
    private final PublicUrlService publicUrlService;
    private final DividendCalculatorService dividendService;
    private final FairValueCalculatorService fairValueService;
    private final LoanCalculatorService loanService;
    private final LoanRefinanceCalculatorService loanRefinanceService;
    private final MortgageCalculatorService mortgageService;
    private final SalaryCalculatorService salaryService;
    private final SeveranceCalculatorService severanceService;
    private final AnnualLeaveCalculatorService annualLeaveService;
    private final ExchangeCalculatorService exchangeService;
    private final OverseasStockTaxCalculatorService overseasStockTaxService;
    private final StockAverageCalculatorService stockAverageService;

    public CalculatorController(SeoService seoService,
                                PublicUrlService publicUrlService,
                                DividendCalculatorService dividendService,
                                FairValueCalculatorService fairValueService,
                                LoanCalculatorService loanService,
                                LoanRefinanceCalculatorService loanRefinanceService,
                                MortgageCalculatorService mortgageService,
                                SalaryCalculatorService salaryService,
                                SeveranceCalculatorService severanceService,
                                AnnualLeaveCalculatorService annualLeaveService,
                                ExchangeCalculatorService exchangeService,
                                OverseasStockTaxCalculatorService overseasStockTaxService,
                                StockAverageCalculatorService stockAverageService) {
        this.seoService = seoService;
        this.publicUrlService = publicUrlService;
        this.dividendService = dividendService;
        this.fairValueService = fairValueService;
        this.loanService = loanService;
        this.loanRefinanceService = loanRefinanceService;
        this.mortgageService = mortgageService;
        this.salaryService = salaryService;
        this.severanceService = severanceService;
        this.annualLeaveService = annualLeaveService;
        this.exchangeService = exchangeService;
        this.overseasStockTaxService = overseasStockTaxService;
        this.stockAverageService = stockAverageService;
    }

    @GetMapping("/dividend-calculator")
    public String dividend(Model model) {
        prepare(model, "dividend", dividendFaqs());
        model.addAttribute("form", new DividendRequest());
        return "dividend-calculator";
    }

    @PostMapping("/dividend-calculator")
    public String calculateDividend(@Valid @ModelAttribute("form") DividendRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "dividend", dividendFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", dividendService.calculate(form));
        return "dividend-calculator";
    }

    @GetMapping("/stock-average-calculator")
    public String stockAverage(Model model) {
        prepare(model, "stock-average", stockAverageFaqs());
        model.addAttribute("form", new StockAverageRequest());
        return "stock-average-calculator";
    }

    @PostMapping("/stock-average-calculator")
    public String calculateStockAverage(@Valid @ModelAttribute("form") StockAverageRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "stock-average", stockAverageFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", stockAverageService.calculate(form));
        return "stock-average-calculator";
    }

    @GetMapping("/fair-value-calculator")
    public String fairValue(Model model) {
        prepare(model, "fair-value", fairValueFaqs());
        model.addAttribute("form", new FairValueRequest());
        return "fair-value-calculator";
    }

    @PostMapping("/fair-value-calculator")
    public String calculateFairValue(@Valid @ModelAttribute("form") FairValueRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "fair-value", fairValueFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", fairValueService.calculate(form));
        return "fair-value-calculator";
    }

    @GetMapping("/loan-interest-calculator")
    public String loan(Model model) {
        prepare(model, "loan", loanFaqs());
        model.addAttribute("form", new LoanRequest());
        return "loan-interest-calculator";
    }

    @PostMapping("/loan-interest-calculator")
    public String calculateLoan(@Valid @ModelAttribute("form") LoanRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "loan", loanFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", loanService.calculate(form));
        return "loan-interest-calculator";
    }

    @GetMapping("/loan-refinance-calculator")
    public String loanRefinance(Model model) {
        prepare(model, "loan-refinance", loanRefinanceFaqs());
        model.addAttribute("form", new LoanRefinanceRequest());
        return "loan-refinance-calculator";
    }

    @PostMapping("/loan-refinance-calculator")
    public String calculateLoanRefinance(@Valid @ModelAttribute("form") LoanRefinanceRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "loan-refinance", loanRefinanceFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", loanRefinanceService.calculate(form));
        return "loan-refinance-calculator";
    }

    @GetMapping("/mortgage-monthly-payment-calculator")
    public String mortgage(Model model) {
        prepare(model, "mortgage", mortgageFaqs());
        model.addAttribute("form", new MortgageRequest());
        return "mortgage-monthly-payment-calculator";
    }

    @PostMapping("/mortgage-monthly-payment-calculator")
    public String calculateMortgage(@Valid @ModelAttribute("form") MortgageRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "mortgage", mortgageFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", mortgageService.calculate(form));
        return "mortgage-monthly-payment-calculator";
    }

    @GetMapping("/annual-salary-net-calculator")
    public String annualSalaryNet(Model model) {
        prepare(model, "annual-salary-net", annualSalaryNetFaqs());
        return "annual-salary-net-calculator";
    }

    @GetMapping("/salary-calculator")
    public String salary(Model model) {
        prepare(model, "salary", salaryFaqs());
        model.addAttribute("form", new SalaryRequest());
        return "salary-calculator";
    }

    @PostMapping("/salary-calculator")
    public String calculateSalary(@Valid @ModelAttribute("form") SalaryRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "salary", salaryFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", salaryService.calculate(form));
        return "salary-calculator";
    }

    @GetMapping("/severance-pay-calculator")
    public String severance(Model model) {
        prepare(model, "severance", severanceFaqs());
        model.addAttribute("form", new SeveranceRequest());
        return "severance-pay-calculator";
    }

    @PostMapping("/severance-pay-calculator")
    public String calculateSeverance(@Valid @ModelAttribute("form") SeveranceRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "severance", severanceFaqs());
        if (!bindingResult.hasErrors() && !form.getEndDate().isBefore(form.getStartDate())) {
            model.addAttribute("result", severanceService.calculate(form));
        } else if (form.getEndDate() != null && form.getStartDate() != null && form.getEndDate().isBefore(form.getStartDate())) {
            model.addAttribute("dateError", "퇴사일은 입사일보다 빠를 수 없습니다.");
        }
        return "severance-pay-calculator";
    }

    @GetMapping("/annual-leave-pay-calculator")
    public String annualLeave(Model model) {
        prepare(model, "annual-leave", annualLeaveFaqs());
        model.addAttribute("form", new AnnualLeaveRequest());
        return "annual-leave-pay-calculator";
    }

    @PostMapping("/annual-leave-pay-calculator")
    public String calculateAnnualLeave(@Valid @ModelAttribute("form") AnnualLeaveRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "annual-leave", annualLeaveFaqs());
        if (!bindingResult.hasErrors() && !form.getCalculationDate().isBefore(form.getStartDate())) {
            model.addAttribute("result", annualLeaveService.calculate(form));
        } else if (form.getCalculationDate() != null && form.getStartDate() != null && form.getCalculationDate().isBefore(form.getStartDate())) {
            model.addAttribute("dateError", "계산 기준일은 입사일보다 빠를 수 없습니다.");
        }
        return "annual-leave-pay-calculator";
    }

    @GetMapping("/exchange-calculator")
    public String exchange(Model model) {
        prepare(model, "exchange", exchangeFaqs());
        model.addAttribute("form", new ExchangeRequest());
        return "exchange-calculator";
    }

    @PostMapping("/exchange-calculator")
    public String calculateExchange(@Valid @ModelAttribute("form") ExchangeRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "exchange", exchangeFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", exchangeService.calculate(form));
        return "exchange-calculator";
    }

    @GetMapping("/domestic-stock-tax-calculator")
    public String domesticStockTax(Model model) {
        prepare(model, "domestic-stock-tax", domesticStockTaxFaqs());
        return "domestic-stock-tax-calculator";
    }

    @GetMapping("/overseas-stock-tax-calculator")
    public String overseasTax(Model model) {
        prepare(model, "overseas-tax", overseasTaxFaqs());
        model.addAttribute("form", new OverseasStockTaxRequest());
        return "overseas-stock-tax-calculator";
    }

    @PostMapping("/overseas-stock-tax-calculator")
    public String calculateOverseasTax(@Valid @ModelAttribute("form") OverseasStockTaxRequest form, BindingResult bindingResult, Model model) {
        prepare(model, "overseas-tax", overseasTaxFaqs());
        if (!bindingResult.hasErrors()) model.addAttribute("result", overseasStockTaxService.calculate(form));
        return "overseas-stock-tax-calculator";
    }

    private void prepare(Model model, String key, List<FaqItem> faqs) {
        PageInfo page = SitePages.require(key);
        model.addAttribute("page", page);
        model.addAttribute("activeMenu", page.key());
        model.addAttribute("pageTitle", page.title());
        model.addAttribute("pageDescription", page.description());
        model.addAttribute("canonicalUrl", publicUrlService.absoluteUrl(page.path()));
        model.addAttribute("faqs", faqs);
        model.addAttribute("structuredData", seoService.structuredData(page, faqs));
        addExampleTables(model);
    }

    private void addExampleTables(Model model) {
        model.addAttribute("annualSalaryExamples", annualSalaryExamples());
        model.addAttribute("monthlySalaryExamples", monthlySalaryExamples());
        model.addAttribute("loanExamples", loanExamples());
        model.addAttribute("mortgageExamples", mortgageExamples());
        model.addAttribute("overseasTaxExamples", overseasTaxExamples());
        model.addAttribute("dividendExamples", dividendExamples());
        model.addAttribute("stockAverageExamples", stockAverageExamples());
    }

    private List<SalaryExampleRow> annualSalaryExamples() {
        return List.of(30_000_000L, 40_000_000L, 50_000_000L, 60_000_000L, 70_000_000L, 100_000_000L).stream()
                .map(annualSalary -> {
                    SalaryRequest request = salaryRequest("ANNUAL", annualSalary);
                    SalaryResult result = salaryService.calculate(request);
                    return new SalaryExampleRow(labelWon(annualSalary), result.grossMonthly(), result.netMonthly(), "2026년, 1인·비과세 20만원 기준");
                })
                .toList();
    }

    private List<MonthlySalaryExampleRow> monthlySalaryExamples() {
        return List.of(2_000_000L, 3_000_000L, 4_000_000L, 5_000_000L).stream()
                .map(monthlySalary -> {
                    SalaryResult result = salaryService.calculate(salaryRequest("MONTHLY", monthlySalary));
                    return new MonthlySalaryExampleRow(labelWon(monthlySalary), result.netMonthly(), "4대보험, 소득세, 지방소득세", "2026년, 1인·비과세 20만원 기준");
                })
                .toList();
    }

    private SalaryRequest salaryRequest(String incomeType, long amount) {
        SalaryRequest request = new SalaryRequest();
        request.setIncomeType(incomeType);
        request.setAmount((double) amount);
        request.setTaxFreeAmount(200_000.0);
        request.setDependents(1);
        request.setChildren(0);
        request.setApplyInsurance(true);
        return request;
    }

    private List<LoanExampleRow> loanExamples() {
        return List.of(
                loanExample(10_000_000L, 5.0, 3),
                loanExample(30_000_000L, 5.0, 5),
                loanExample(50_000_000L, 5.0, 5),
                loanExample(100_000_000L, 4.5, 30)
        );
    }

    private LoanExampleRow loanExample(long principal, double rate, int years) {
        LoanRequest request = new LoanRequest();
        request.setPrincipal(principal);
        request.setAnnualRate(rate);
        request.setYears(years);
        request.setRepaymentType("EQUAL_PAYMENT");
        LoanResult result = loanService.calculate(request);
        return new LoanExampleRow(labelWon(principal), rate + "%", years + "년", "원리금균등", result.averageMonthlyPayment(), result.totalInterest());
    }

    private List<MortgageExampleRow> mortgageExamples() {
        return List.of(
                mortgageExample(300_000_000L, 200_000_000L),
                mortgageExample(500_000_000L, 300_000_000L),
                mortgageExample(700_000_000L, 400_000_000L),
                mortgageExample(1_000_000_000L, 500_000_000L)
        );
    }

    private MortgageExampleRow mortgageExample(long housePrice, long loanAmount) {
        MortgageRequest request = new MortgageRequest();
        request.setHousePrice(housePrice);
        request.setCashOnHand(Math.max(0, housePrice - loanAmount));
        request.setExpectedLoanAmount(loanAmount);
        request.setAnnualRate(4.0);
        request.setYears(30);
        request.setRepaymentType("EQUAL_PAYMENT");
        request.setLtvRatio(70.0);
        request.setAnnualIncome(80_000_000L);
        request.setExistingMonthlyDebtPayment(0L);
        MortgageResult result = mortgageService.calculate(request);
        return new MortgageExampleRow(labelWon(housePrice), labelWon(loanAmount), "4.0%", "30년", result.estimatedMonthlyPayment(), loanAmount * 100.0 / housePrice);
    }

    private List<OverseasTaxExampleRow> overseasTaxExamples() {
        return List.of(
                overseasTaxExample(10_000_000, 13_000_000),
                overseasTaxExample(20_000_000, 30_000_000),
                overseasTaxExample(50_000_000, 70_000_000)
        );
    }

    private OverseasTaxExampleRow overseasTaxExample(double buyAmount, double sellAmount) {
        OverseasStockTaxRequest request = new OverseasStockTaxRequest();
        request.setBuyAmountForeign(buyAmount);
        request.setSellAmountForeign(sellAmount);
        request.setDividendForeign(0.0);
        request.setBuyExchangeRate(1.0);
        request.setSellExchangeRate(1.0);
        request.setDividendExchangeRate(1.0);
        request.setFeeKrw(0.0);
        request.setApplyBasicDeduction(true);
        request.setBasicDeductionKrw(2_500_000.0);
        request.setCapitalGainsTaxRate(22.0);
        request.setDividendTaxRate(15.4);
        OverseasStockTaxResult result = overseasStockTaxService.calculate(request);
        return new OverseasTaxExampleRow(labelWon((long) buyAmount), labelWon((long) sellAmount), result.capitalGainKrw(), 2_500_000, result.taxableCapitalGainKrw(), result.capitalGainsTaxKrw());
    }

    private List<DividendExampleRow> dividendExamples() {
        return List.of(
                dividendExample(100L, 500.0, "QUARTERLY", "분기"),
                dividendExample(300L, 500.0, "QUARTERLY", "분기"),
                dividendExample(100L, 1_000.0, "MONTHLY", "월")
        );
    }

    private DividendExampleRow dividendExample(long shares, double dividendPerShare, String period, String label) {
        DividendRequest request = new DividendRequest();
        request.setShares(shares);
        request.setDividendPerShare(dividendPerShare);
        request.setPeriod(period);
        request.setTaxApplied(true);
        request.setTaxRate(15.4);
        DividendResult result = dividendService.calculate(request);
        return new DividendExampleRow(shares + "주", labelWon((long) dividendPerShare), label, result.oneTimeGross(), result.oneTimeNet());
    }

    private List<StockAverageExampleRow> stockAverageExamples() {
        return List.of(
                stockAverageExample(100L, 50_000.0, 50L, 40_000.0),
                stockAverageExample(100L, 50_000.0, 100L, 40_000.0),
                stockAverageExample(100L, 50_000.0, 200L, 35_000.0)
        );
    }

    private StockAverageExampleRow stockAverageExample(long currentShares, double currentAveragePrice, long additionalShares, double additionalPrice) {
        StockAverageRequest request = new StockAverageRequest();
        request.setCurrentShares(currentShares);
        request.setCurrentAveragePrice(currentAveragePrice);
        request.setAdditionalShares(additionalShares);
        request.setAdditionalPrice(additionalPrice);
        StockAverageResult result = stockAverageService.calculate(request);
        return new StockAverageExampleRow(currentShares + "주", labelWon((long) currentAveragePrice), additionalShares + "주", labelWon((long) additionalPrice), result.newAveragePrice());
    }

    private String labelWon(long value) {
        if (value >= 100_000_000 && value % 100_000_000 == 0) {
            return (value / 100_000_000) + "억원";
        }
        if (value >= 10_000_000 && value % 10_000_000 == 0) {
            return (value / 10_000_000) + ",000만원";
        }
        return String.format("%,d원", value);
    }

    public record SalaryExampleRow(String annualSalary, double grossMonthly, double netMonthly, String basis) {}
    public record MonthlySalaryExampleRow(String monthlySalary, double netMonthly, String deductions, String basis) {}
    public record LoanExampleRow(String principal, String rate, String years, String repaymentType, double monthlyPayment, double totalInterest) {}
    public record MortgageExampleRow(String housePrice, String loanAmount, String rate, String years, double monthlyPayment, double ltv) {}
    public record OverseasTaxExampleRow(String buyAmount, String sellAmount, double capitalGain, double deduction, double taxableGain, double tax) {}
    public record DividendExampleRow(String shares, String dividendPerShare, String period, double grossDividend, double netDividend) {}
    public record StockAverageExampleRow(String currentShares, String currentAveragePrice, String additionalShares, String additionalPrice, double newAveragePrice) {}

    private List<FaqItem> dividendFaqs() {
        return List.of(
                new FaqItem("세후 배당금은 어떻게 계산하나요?", "1회 배당금에 원천징수 세율을 차감하고, 배당 주기에 따라 월·연 기준으로 환산합니다."),
                new FaqItem("월배당과 분기배당을 모두 계산할 수 있나요?", "배당 주기를 월, 분기, 반기, 연 단위로 선택할 수 있습니다."),
                new FaqItem("해외주식 배당세도 반영되나요?", "세율을 직접 입력할 수 있어 국가별 원천징수율을 참고해 입력할 수 있습니다.")
        );
    }

    private List<FaqItem> stockAverageFaqs() {
        return List.of(
                new FaqItem("물타기 후 평균단가는 어떻게 계산하나요?", "기존 보유금액과 추가 매수금액을 더한 뒤 총 보유 수량으로 나누어 추가매수 후 평균단가를 계산합니다."),
                new FaqItem("수수료와 세금도 반영되나요?", "현재 버전은 보유 수량, 기존 평균단가, 추가 매수 수량, 추가 매수 단가만 반영합니다. 실제 손익은 거래 수수료, 제세금, 환율에 따라 달라질 수 있습니다."),
                new FaqItem("계산 결과가 매수 추천인가요?", "아닙니다. 물타기 계산 결과는 평균단가 확인용 참고값이며 투자 권유나 종목 추천이 아닙니다.")
        );
    }

    private List<FaqItem> fairValueFaqs() {
        return List.of(
                new FaqItem("적정주가는 투자 권유인가요?", "아닙니다. EPS와 PER 등 단순 입력값을 바탕으로 계산한 참고용 수치입니다."),
                new FaqItem("안전마진은 무엇인가요?", "계산된 적정가에서 일정 비율을 할인해 보수적인 매수가를 구하기 위한 값입니다."),
                new FaqItem("성장률과 할인율은 어떻게 쓰이나요?", "기본 EPS×PER 값에 성장률을 반영하고 할인율로 현재가치 관점의 보정값을 적용합니다.")
        );
    }

    private List<FaqItem> loanFaqs() {
        return List.of(
                new FaqItem("원리금균등과 원금균등의 차이는 무엇인가요?", "원리금균등은 매월 비슷한 금액을 내고, 원금균등은 매월 같은 원금을 갚아 초기 상환액이 큽니다."),
                new FaqItem("원리금균등과 원금균등 중 어떤 방식이 유리한가요?", "원금균등은 초기 월 납입액이 크지만 총이자가 줄어드는 경향이 있습니다. 원리금균등은 매월 납입액이 일정해 현금흐름 관리가 쉽습니다."),
                new FaqItem("만기일시상환은 어떤 경우에 사용하나요?", "만기일시상환은 매월 이자만 내고 만기에 원금을 한 번에 갚는 방식입니다. 단기 자금 운용에는 유리할 수 있지만 만기 원금 상환 부담이 큽니다."),
                new FaqItem("월별 상환표를 내려받을 수 있나요?", "결과 표의 CSV 다운로드 버튼으로 브라우저에서 상환표를 저장할 수 있습니다."),
                new FaqItem("중도상환수수료도 반영되나요?", "초기 버전은 기본 상환 방식 중심이며, 중도상환수수료는 2차 기능으로 확장할 수 있습니다.")
        );
    }

    private List<FaqItem> loanRefinanceFaqs() {
        return List.of(
                new FaqItem("손익분기점 개월 수는 어떻게 계산하나요?", "중도상환수수료와 기타 비용을 월 절감액으로 나누어 비용을 회수하는 데 걸리는 개월 수를 계산합니다."),
                new FaqItem("기존 대출과 신규 대출 기간이 달라도 되나요?", "네. 현재 남은 기간과 신규 대출 기간을 각각 입력해 총 이자와 월 납입액을 비교합니다."),
                new FaqItem("추천 여부는 실제 대출 심사 결과인가요?", "아닙니다. 실제 절감액과 손익분기점 기준의 임시 판단이며, 실제 갈아타기 전에는 금융사 조건을 확인해야 합니다.")
        );
    }

    private List<FaqItem> mortgageFaqs() {
        return List.of(
                new FaqItem("LTV는 무엇인가요?", "LTV는 주택 가격 대비 대출금액 비율입니다. 예를 들어 5억원 주택에 3억원을 대출받으면 LTV는 60%입니다."),
                new FaqItem("월 상환 부담률은 왜 중요한가요?", "월 상환 부담률은 소득 대비 대출 상환액의 비중을 보여줍니다. 비율이 높을수록 생활비나 비상자금 여력이 줄어들 수 있습니다."),
                new FaqItem("예상 월 납입금은 어떤 값을 보여주나요?", "원리금균등은 고정 월 납입금, 원금균등은 첫 달 납입금, 만기일시는 매월 이자 납입금을 보여줍니다."),
                new FaqItem("위험도는 어떤 부담률을 기준으로 하나요?", "기존 대출 월 상환액을 포함한 월 상환 부담률을 기준으로 안전, 주의, 위험을 임시 분류합니다."),
                new FaqItem("LTV 기준 최대 대출 가능 금액은 실제 승인 금액인가요?", "아닙니다. 입력한 주택 가격과 LTV 비율만 반영한 단순 계산값이며, 실제 한도는 DSR, 지역, 소득, 금융사 심사에 따라 달라집니다.")
        );
    }

    private List<FaqItem> annualSalaryNetFaqs() {
        return List.of(
                new FaqItem("정확한 급여명세서 금액과 같나요?", "아닙니다. 이 계산기는 예상 금액을 빠르게 확인하기 위한 참고용 도구입니다. 실제 급여명세서 금액은 회사별 급여 정책, 비과세 항목, 공제 항목, 최신 세법에 따라 달라질 수 있습니다."),
                new FaqItem("퇴직금 포함 여부는 어떻게 반영하나요?", "퇴직금 포함을 선택하면 연봉을 13개월 기준으로 나누어 월 급여를 추정하고, 미포함을 선택하면 12개월 기준으로 월 급여를 추정합니다."),
                new FaqItem("계산 기준은 언제 업데이트되나요?", "4대보험 요율, 국민연금 상·하한액, 근로소득세 기준 등 주요 기준이 변경될 때 업데이트합니다.")
        );
    }

    private List<FaqItem> salaryFaqs() {
        return List.of(
                new FaqItem("실수령액은 정확한 급여명세서와 같나요?", "회사별 비과세 항목, 가족 수, 자녀세액공제, 원천징수 정책에 따라 달라질 수 있습니다. 이 페이지는 2026년 기준 세율과 간이세액표 구조를 반영한 추정치입니다."),
                new FaqItem("가족 수는 어떻게 입력하나요?", "국세청 간이세액표 기준과 동일하게 공제대상 가족 수는 본인을 포함해 입력합니다."),
                new FaqItem("산재보험이 안 보이는 이유는 무엇인가요?", "산재보험은 통상 사업주 전액 부담 항목이므로 근로자 실수령액 공제 항목에는 넣지 않았습니다."),
                new FaqItem("연봉 입력도 가능한가요?", "월급 또는 연봉 기준을 선택할 수 있으며, 연봉 선택 시 12개월로 나누어 월 실수령액을 계산합니다.")
        );
    }

    private List<FaqItem> severanceFaqs() {
        return List.of(
                new FaqItem("1년 미만 근무자도 퇴직금이 나오나요?", "근로자퇴직급여 보장법상 계속근로기간 1년 미만이면 퇴직금이 발생하지 않으므로 이 계산기도 재직일수 365일 미만이면 0원으로 표시합니다."),
                new FaqItem("평균임금은 어떻게 계산하나요?", "퇴직 전 3개월 임금 총액에 연간 상여금 3/12, 직전 1년 발생 연차수당 3/12를 더한 뒤 직전 3개월의 실제 총일수로 나눠 계산합니다."),
                new FaqItem("통상임금도 반영되나요?", "네. 계산된 평균임금이 입력한 1일 통상임금보다 낮으면 통상임금을 적용해 하한을 보정합니다.")
        );
    }

    private List<FaqItem> annualLeaveFaqs() {
        return List.of(
                new FaqItem("발생 연차는 어떻게 계산하나요?", "근로기준법 제60조의 기본 구조를 따라 1년 미만 또는 80% 미만 출근자는 1개월 개근 시 1일, 1년 이상 80% 이상 출근자는 15일을 전제로 계산합니다."),
                new FaqItem("연차수당은 어떻게 계산하나요?", "잔여 연차일수에 1일 통상임금을 곱해 계산합니다. 별도의 규정이 없으면 통상임금을 기준으로 보는 구조를 기본값으로 두었습니다."),
                new FaqItem("회사 정책과 다른 경우가 있나요?", "네. 회계연도 기준 운영, 사용촉진, 출근율, 단시간 근로 여부에 따라 실제 부여일수와 수당은 달라질 수 있습니다.")
        );
    }

    private List<FaqItem> exchangeFaqs() {
        return List.of(
                new FaqItem("실시간 환율을 자동 반영하나요?", "초기 버전은 환율을 직접 입력하는 방식입니다."),
                new FaqItem("환전 수수료는 어떻게 계산하나요?", "환전 전 금액에 수수료율을 곱해 차감한 결과를 보여줍니다."),
                new FaqItem("통화 단위는 제한되나요?", "기본 통화 목록을 제공하며, 환율만 알면 어떤 통화 조합도 계산 로직을 확장할 수 있습니다.")
        );
    }

    private List<FaqItem> overseasTaxFaqs() {
        return List.of(
                new FaqItem("해외주식 양도세 기본공제를 반영하나요?", "네. 2026년 기준 연간 기본공제 250만원을 기본값으로 넣었고, 필요하면 직접 수정할 수 있습니다."),
                new FaqItem("해외주식 양도소득세 기본공제는 얼마인가요?", "해외주식 양도차익은 기본공제를 차감한 뒤 과세되는 구조입니다. 다만 실제 신고 기준은 세법 개정이나 개인 상황에 따라 달라질 수 있으므로 최신 기준을 확인해야 합니다."),
                new FaqItem("배당세와 양도세는 다른가요?", "배당세는 배당금을 받을 때 적용되는 세금이고, 양도세는 주식을 매도해 차익이 발생했을 때 적용되는 세금입니다."),
                new FaqItem("양도세율 기본값 22%는 무엇인가요?", "기본값 22%는 국세 20%와 지방소득세 2%를 합친 값입니다. 중소기업 특례 등 예외가 있으면 직접 수정해 계산할 수 있습니다."),
                new FaqItem("배당세 15.4%는 언제 쓰나요?", "국내 원천징수 기준의 기본값입니다. 다만 연간 금융소득이 2천만원을 초과하거나 외국납부세액공제가 필요한 경우 실제 신고세액은 달라질 수 있습니다."),
                new FaqItem("실제 신고세액과 같나요?", "아닙니다. 실제 신고는 거래일별 기준환율, 손익통산, 외국납부세액공제, 최신 세법에 따라 달라질 수 있습니다.")
        );
    }

    private List<FaqItem> domesticStockTaxFaqs() {
        return List.of(
                new FaqItem("국내 주식 매도세금은 무엇을 계산하나요?", "매도금액에 증권거래세율을 곱해 예상 증권거래세를 계산하고, 대주주 등 양도소득세 대상 여부는 별도 입력으로 확인합니다."),
                new FaqItem("국내 상장주식도 양도소득세가 발생하나요?", "일반적인 소액주주 상장주식 거래는 양도소득세가 과세되지 않는 경우가 많지만, 대주주나 비상장주식 등 요건에 따라 양도소득세가 발생할 수 있습니다."),
                new FaqItem("해외주식과 계산 방식이 다른가요?", "해외주식은 양도차익에서 기본공제 250만원을 차감한 과세표준에 세율을 적용하는 구조라 국내주식 증권거래세 계산과 다릅니다.")
        );
    }
}
