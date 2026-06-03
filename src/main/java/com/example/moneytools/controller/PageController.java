package com.example.moneytools.controller;

import com.example.moneytools.seo.FaqItem;
import com.example.moneytools.seo.PageInfo;
import com.example.moneytools.seo.PublicUrlService;
import com.example.moneytools.seo.SeoService;
import com.example.moneytools.seo.SitePages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {
    private final SeoService seoService;
    private final PublicUrlService publicUrlService;

    public PageController(SeoService seoService, PublicUrlService publicUrlService) {
        this.seoService = seoService;
        this.publicUrlService = publicUrlService;
    }

    @GetMapping("/")
    public String home(Model model) {
        PageInfo page = SitePages.require("home");
        prepare(model, page, List.of(
                new FaqItem("계산 결과를 저장하나요?", "현재 버전은 별도 회원가입이나 서버 저장 없이 입력값으로만 계산 결과를 보여줍니다."),
                new FaqItem("세금과 급여 계산은 정확한가요?", "간이 계산 목적입니다. 실제 신고, 급여 정산, 투자 판단 전에는 최신 법령과 전문가 검토가 필요합니다."),
                new FaqItem("광고가 계산 버튼처럼 보이지 않나요?", "광고 영역은 콘텐츠와 구분되도록 설계되어 있으며, 사용자의 계산 흐름을 방해하지 않는 위치에 배치합니다."),
                new FaqItem("정보성 글도 제공하나요?", "네. 계산기만 나열하지 않고 실수령액, 대출 상환, 전기요금, 해외주식 세금처럼 검색 수요가 큰 주제를 설명형 가이드로 함께 제공합니다."),
                new FaqItem("어떤 페이지를 먼저 보면 좋나요?", "생활비 계획이 필요하면 실수령액과 월 생활비 계산기부터, 대출이 고민이면 대출이자 계산기와 상환 방식 가이드부터 보는 것이 좋습니다.")
        ));
        model.addAttribute("calculatorPages", SitePages.navigation().stream()
                .filter(p -> !List.of("home", "guide", "privacy", "contact").contains(p.key()))
                .toList());
        model.addAttribute("guidePages", SitePages.guides());
        return "index";
    }

    @GetMapping("/guide")
    public String guide(Model model) {
        PageInfo page = SitePages.require("guide");
        prepare(model, page, List.of(
                new FaqItem("이 사이트의 계산 결과를 공식 자료로 제출해도 되나요?", "아니요. 계산 결과는 참고용이며 공식 신고·증빙 자료가 아닙니다."),
                new FaqItem("2026년 기준은 어디까지 반영됐나요?", "4대보험 근로자 부담률, 국민연금 상·하한액, 현재 세율 구조, 해외주식 기본공제, 퇴직금·연차의 핵심 법정 계산 구조를 프로젝트에 반영했습니다."),
                new FaqItem("가이드 글은 어떤 기준으로 작성하나요?", "사용자가 계산기 결과를 이해하는 데 직접 필요한 질문 중심으로, 실제 예시와 자주 하는 실수를 함께 넣는 편집 원칙을 사용합니다."),
                new FaqItem("짧은 페이지는 어떻게 처리하나요?", "콘텐츠가 충분하지 않은 페이지는 보강하거나 검색 유입 가치가 낮으면 색인 우선순위를 조정하는 방식으로 관리합니다.")
        ));
        model.addAttribute("guidePages", SitePages.guides());
        return "guide";
    }

    @GetMapping("/stock-tax-calculator")
    public String stockTax(Model model) {
        PageInfo page = SitePages.require("stock-tax");
        prepare(model, page, List.of(
                new FaqItem("국내주식과 해외주식 매도세금은 어떻게 다른가요?", "국내 상장주식 일반 투자자는 보통 증권거래세 중심으로 확인하고, 해외주식은 양도차익에서 기본공제 250만원을 차감한 뒤 양도소득세를 계산합니다."),
                new FaqItem("주식 매도 세금 계산은 어디서 하나요?", "국내주식은 국내 주식 세금 계산기에서 매도금액 기준 증권거래세를 확인하고, 해외주식은 해외주식 세금 계산기에서 양도소득세와 배당세를 함께 확인할 수 있습니다."),
                new FaqItem("국내주식 매도세금은 매도할 때마다 발생하나요?", "증권거래세는 일반적으로 국내주식을 매도할 때 매도금액을 기준으로 계산합니다. 실제 부담은 시장, 종목, 계좌 유형, 세법 개정에 따라 달라질 수 있습니다."),
                new FaqItem("해외주식 세금은 수익이 250만원 이하이면 안 내도 되나요?", "해외주식 양도차익은 연간 손익을 합산한 뒤 기본공제 250만원을 적용합니다. 계산상 과세표준이 0원이 될 수 있지만 신고 대상 여부와 실제 세액은 최신 기준을 확인해야 합니다."),
                new FaqItem("주식 손실이 있으면 세금 계산에서 반영되나요?", "해외주식은 같은 과세기간의 손익통산을 검토할 수 있습니다. 국내주식과 해외주식, 배당소득은 계산 기준이 다르므로 손실 반영 가능 여부를 구분해야 합니다."),
                new FaqItem("배당소득세와 주식 매도세금은 함께 계산해야 하나요?", "배당소득세는 배당을 받을 때, 매도세금은 주식을 팔 때 발생합니다. 전체 투자 세금을 보려면 배당금과 매도차익을 각각 계산한 뒤 함께 확인하는 것이 좋습니다.")
        ));
        return "stock-tax-calculator";
    }

    @GetMapping("/guide/stock-tax")
    public String stockTaxGuide(Model model) {
        PageInfo page = SitePages.require("guide-stock-tax");
        prepare(model, page, stockTaxGuideFaqs());
        return "guide-stock-tax";
    }

    @GetMapping("/guide/overseas-stock-tax")
    public String overseasStockTaxGuide(Model model) {
        PageInfo page = SitePages.require("guide-overseas-stock-tax");
        prepare(model, page, overseasStockTaxGuideFaqs());
        return "guide-overseas-stock-tax";
    }

    @GetMapping("/guide/dividend-tax")
    public String dividendTaxGuide(Model model) {
        PageInfo page = SitePages.require("guide-dividend-tax");
        prepare(model, page, dividendTaxGuideFaqs());
        return "guide-dividend-tax";
    }

    @GetMapping("/guide/salary-5000-net")
    public String salary5000Guide(Model model) {
        PageInfo page = SitePages.require("guide-salary-5000");
        prepare(model, page, salary5000GuideFaqs());
        return "guide-salary-5000-net";
    }

    @GetMapping("/guide/salary-3000-net")
    public String salary3000Guide(Model model) {
        PageInfo page = SitePages.require("guide-salary-3000");
        prepare(model, page, salary3000GuideFaqs());
        return "guide-salary-3000-net";
    }

    @GetMapping("/guide/salary-100m-net")
    public String salary100mGuide(Model model) {
        PageInfo page = SitePages.require("guide-salary-100m");
        prepare(model, page, salary100mGuideFaqs());
        return "guide-salary-100m-net";
    }

    @GetMapping("/guide/monthly-salary-300-net")
    public String monthlySalary300Guide(Model model) {
        PageInfo page = SitePages.require("guide-monthly-salary-300");
        prepare(model, page, monthlySalary300GuideFaqs());
        return "guide-monthly-salary-300-net";
    }

    @GetMapping("/guide/loan-100m-interest")
    public String loan100mGuide(Model model) {
        PageInfo page = SitePages.require("guide-loan-100m-interest");
        prepare(model, page, loan100mGuideFaqs());
        return "guide-loan-100m-interest";
    }

    @GetMapping("/guide/repayment-method-difference")
    public String repaymentDifferenceGuide(Model model) {
        PageInfo page = SitePages.require("guide-repayment-difference");
        prepare(model, page, repaymentDifferenceGuideFaqs());
        return "guide-repayment-method-difference";
    }

    @GetMapping("/guide/ltv-dsr-difference")
    public String ltvDsrGuide(Model model) {
        PageInfo page = SitePages.require("guide-ltv-dsr");
        prepare(model, page, ltvDsrGuideFaqs());
        return "guide-ltv-dsr-difference";
    }

    @GetMapping("/guide/severance-average-wage")
    public String severanceAverageWageGuide(Model model) {
        PageInfo page = SitePages.require("guide-severance-average-wage");
        prepare(model, page, severanceAverageWageGuideFaqs());
        return "guide-severance-average-wage";
    }

    @GetMapping("/guide/overseas-stock-tax-deduction")
    public String overseasDeductionGuide(Model model) {
        PageInfo page = SitePages.require("guide-overseas-deduction");
        prepare(model, page, overseasDeductionGuideFaqs());
        return "guide-overseas-stock-tax-deduction";
    }

    @GetMapping("/guide/electricity-tier")
    public String electricityTierGuide(Model model) {
        PageInfo page = SitePages.require("guide-electricity-tier");
        prepare(model, page, electricityTierGuideFaqs());
        return "guide-electricity-tier";
    }

    @GetMapping("/guide/aircon-8hours-cost")
    public String airconGuide(Model model) {
        PageInfo page = SitePages.require("guide-aircon-8hours");
        prepare(model, page, airconGuideFaqs());
        return "guide-aircon-8hours-cost";
    }

    @GetMapping("/guide/car-monthly-cost")
    public String carCostGuide(Model model) {
        PageInfo page = SitePages.require("guide-car-cost");
        prepare(model, page, carCostGuideFaqs());
        return "guide-car-monthly-cost";
    }

    @GetMapping("/guide/dividend-100man")
    public String dividend100Guide(Model model) {
        PageInfo page = SitePages.require("guide-dividend-100m");
        prepare(model, page, dividend100GuideFaqs());
        return "guide-dividend-100man";
    }

    @GetMapping("/guide/monthly-budget-items")
    public String budgetItemsGuide(Model model) {
        PageInfo page = SitePages.require("guide-budget-items");
        prepare(model, page, budgetItemsGuideFaqs());
        return "guide-monthly-budget-items";
    }

    @GetMapping("/guide/per-fair-value-limit")
    public String perFairValueLimitGuide(Model model) {
        PageInfo page = SitePages.require("guide-per-limit");
        prepare(model, page, perFairValueLimitGuideFaqs());
        return "guide-per-fair-value-limit";
    }

    @GetMapping("/privacy-policy")
    public String privacy(Model model) {
        PageInfo page = SitePages.require("privacy");
        prepare(model, page, List.of());
        return "privacy-policy";
    }

    @GetMapping("/privacy")
    public String privacyPolicyRedirect() {
        return "redirect:/privacy-policy";
    }

    @GetMapping("/about")
    public String about(Model model) {
        PageInfo page = SitePages.require("about");
        prepare(model, page, List.of());
        return "about";
    }

    @GetMapping("/terms")
    public String terms(Model model) {
        PageInfo page = SitePages.require("terms");
        prepare(model, page, List.of());
        return "terms";
    }

    @GetMapping("/disclaimer")
    public String disclaimer(Model model) {
        PageInfo page = SitePages.require("disclaimer");
        prepare(model, page, List.of());
        return "disclaimer";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        PageInfo page = SitePages.require("contact");
        prepare(model, page, List.of());
        return "contact";
    }

    private void prepare(Model model, PageInfo page, List<FaqItem> faqs) {
        model.addAttribute("page", page);
        model.addAttribute("activeMenu", page.key());
        model.addAttribute("pageTitle", page.title());
        model.addAttribute("pageDescription", page.description());
        model.addAttribute("canonicalUrl", publicUrlService.absoluteUrl(page.path()));
        model.addAttribute("faqs", faqs);
        model.addAttribute("structuredData", seoService.structuredData(page, faqs));
    }

    private List<FaqItem> stockTaxGuideFaqs() {
        return List.of(
                new FaqItem("국내주식과 해외주식 세금 계산기를 따로 써야 하나요?", "네. 국내주식은 매도금액 기준 증권거래세 확인이 중심이고, 해외주식은 양도차익과 기본공제 250만원을 반영해야 하므로 계산 흐름이 다릅니다."),
                new FaqItem("국내 상장주식은 양도소득세가 항상 없나요?", "항상 그렇지는 않습니다. 대주주, 비상장주식, 장외거래 등 요건에 해당하면 양도소득세 검토가 필요할 수 있습니다."),
                new FaqItem("해외주식 양도세 기본공제는 거래마다 적용하나요?", "거래마다가 아니라 연간 해외주식 양도차익을 합산한 뒤 기본공제를 적용하는 방식으로 이해해야 합니다."),
                new FaqItem("배당소득세는 매도세금과 다른가요?", "다릅니다. 배당소득세는 주식을 보유하면서 배당금을 받을 때 적용되고, 매도세금은 주식을 팔 때 발생합니다."),
                new FaqItem("손실 종목이 있으면 해외주식 세금이 줄어드나요?", "같은 과세기간의 해외주식 손익통산을 검토할 수 있습니다. 실제 신고 전 거래내역 전체를 기준으로 확인해야 합니다."),
                new FaqItem("최신 기준 확인이 필요한가요?", "필요합니다. 세율, 대주주 기준, 신고 방식은 개정될 수 있으므로 실제 신고 전에는 공식 안내를 확인해야 합니다.")
        );
    }

    private List<FaqItem> overseasStockTaxGuideFaqs() {
        return List.of(
                new FaqItem("양도차익이 250만원보다 작으면 세금이 없나요?", "계산상 과세표준은 0원이 될 수 있습니다. 다만 연간 전체 해외주식 손익과 신고 대상 여부는 실제 거래내역 기준으로 확인해야 합니다."),
                new FaqItem("기본공제는 종목별로 적용하나요?", "아닙니다. 일반적으로 연간 해외주식 양도차익을 합산한 뒤 기본공제를 적용하는 흐름으로 봅니다."),
                new FaqItem("환율은 어떤 값을 입력해야 하나요?", "실제 신고에서는 거래일별 기준환율 등 적용 기준을 확인해야 합니다. 계산기는 매수일과 매도일 환율을 직접 입력해 참고값을 구합니다."),
                new FaqItem("수수료는 양도차익에서 뺄 수 있나요?", "계산기에서는 매매 수수료 합계를 필요경비처럼 차감해 예상 양도차익을 계산합니다. 실제 인정 범위는 신고 기준을 확인해야 합니다."),
                new FaqItem("배당세도 같은 방식으로 계산하나요?", "아닙니다. 배당세는 배당금을 받을 때 원천징수되는 세금이고, 양도세는 주식을 팔아 차익이 생겼을 때 계산하는 세금입니다."),
                new FaqItem("최신 기준 확인이 필요한가요?", "필요합니다. 해외주식 세금은 세법, 신고 절차, 외국납부세액공제 처리에 따라 실제 신고세액이 달라질 수 있습니다.")
        );
    }

    private List<FaqItem> dividendTaxGuideFaqs() {
        return List.of(
                new FaqItem("세율 15.4%는 항상 같은가요?", "국내 배당 원천징수의 일반적인 기본값으로 볼 수 있지만, 해외 배당, 금융소득종합과세, 외국납부세액공제 여부에 따라 실제 세금은 달라질 수 있습니다."),
                new FaqItem("월배당은 매월 같은 금액을 받나요?", "종목과 ETF의 배당 정책에 따라 매월 배당금이 달라질 수 있습니다. 계산기는 입력한 주당 배당금을 기준으로 단순 환산합니다."),
                new FaqItem("분기배당은 연 배당금으로 어떻게 환산하나요?", "1회 세후 배당금에 4를 곱해 연 세후 배당금을 추정합니다. 실제 지급월과 배당금 변동은 종목별 공시를 확인해야 합니다."),
                new FaqItem("해외주식 배당세도 계산할 수 있나요?", "계산기에서 세율을 직접 입력할 수 있으므로 해외 원천징수율을 참고해 세후 배당금을 추정할 수 있습니다."),
                new FaqItem("금융소득종합과세는 반영되나요?", "단순 배당 계산기는 원천징수 기준의 세후 금액을 보여줍니다. 금융소득종합과세 대상 여부는 연간 금융소득 전체를 기준으로 별도 확인해야 합니다."),
                new FaqItem("최신 기준 확인이 필요한가요?", "필요합니다. 배당 원천징수율, 금융소득종합과세 기준, 해외 배당 처리 방식은 실제 신고 시점의 기준을 확인해야 합니다.")
        );
    }

    private List<FaqItem> salary5000GuideFaqs() {
        return List.of(
                new FaqItem("연봉 5,000만 원이면 월 세전 급여는 얼마인가요?", "일반적으로 12개월 기준 약 416만 원 수준으로 보지만 상여 구조와 퇴직금 포함 여부에 따라 달라질 수 있습니다."),
                new FaqItem("실수령액이 사람마다 다른 이유는 무엇인가요?", "부양가족 수, 자녀 수, 비과세 식대, 회사 복리후생, 원천징수 방식이 다르기 때문입니다."),
                new FaqItem("상여금이 많으면 불리한가요?", "과세 방식과 지급 주기에 따라 월 실수령액 체감이 달라질 수 있어 연간 기준과 월 기준을 함께 봐야 합니다."),
                new FaqItem("세후 월급만 보면 충분한가요?", "아닙니다. 연 실수령액, 고정비, 저축 목표까지 함께 보아야 생활비 계획이 쉬워집니다."),
                new FaqItem("어떤 계산기로 연결되나요?", "실수령액 계산기와 월 생활비 계산기를 함께 보면 실제 가계 예산을 잡기 좋습니다.")
        );
    }

    private List<FaqItem> salary3000GuideFaqs() {
        return List.of(
                new FaqItem("연봉 3,000만 원이면 월 세전 급여는 얼마인가요?", "12개월 기준 약 250만 원 수준으로 볼 수 있습니다."),
                new FaqItem("실수령액이 왜 체감상 더 적게 느껴지나요?", "4대보험과 세금 공제 후 실제 생활비에 쓸 돈이 줄어들기 때문입니다."),
                new FaqItem("초봉 구간에서 가장 중요한 지표는 무엇인가요?", "세후 월급 대비 주거비와 고정비 비율입니다."),
                new FaqItem("비과세 식대가 있으면 차이가 큰가요?", "저연봉 구간일수록 체감 차이가 상대적으로 분명하게 느껴질 수 있습니다."),
                new FaqItem("어떤 계산기를 같이 보면 좋나요?", "실수령액 계산기와 월 생활비 계산기를 함께 보는 것이 좋습니다.")
        );
    }

    private List<FaqItem> salary100mGuideFaqs() {
        return List.of(
                new FaqItem("연봉 1억 원이면 월 세전 급여는 얼마인가요?", "12개월 기준 약 833만 원 수준입니다."),
                new FaqItem("왜 세전과 세후 차이가 크게 느껴지나요?", "소득세와 각종 공제로 공제 규모가 함께 커지기 때문입니다."),
                new FaqItem("실수령액만 보면 충분한가요?", "아닙니다. 세후 월급과 함께 세후 연간 현금흐름도 같이 봐야 합니다."),
                new FaqItem("상여 구조가 있으면 결과가 달라지나요?", "정기 상여와 성과급 지급 시점에 따라 월 체감 현금흐름은 달라집니다."),
                new FaqItem("어떤 계산기로 연결되나요?", "연봉 실수령액 계산기와 월 생활비 계산기를 연결해 보는 것이 좋습니다.")
        );
    }

    private List<FaqItem> monthlySalary300GuideFaqs() {
        return List.of(
                new FaqItem("월급 300만 원이면 실수령액은 얼마쯤인가요?", "가족 수와 비과세 항목에 따라 달라지지만 세전보다 낮은 금액이 실제 수령액입니다."),
                new FaqItem("고정비를 어느 정도로 잡아야 하나요?", "주거비와 대출 상환액이 과도하면 체감 여유자금이 빠르게 줄 수 있습니다."),
                new FaqItem("식대와 복지포인트도 영향이 있나요?", "과세 여부에 따라 실수령액 체감이 달라질 수 있습니다."),
                new FaqItem("적정 생활비를 보려면 무엇이 필요하나요?", "세후 월급과 월 생활비 구조를 함께 봐야 합니다."),
                new FaqItem("관련 계산기는 무엇인가요?", "월급 실수령액 계산기와 월 생활비 계산기입니다.")
        );
    }

    private List<FaqItem> loan100mGuideFaqs() {
        return List.of(
                new FaqItem("대출 1억 원 연 5%면 이자만 얼마인가요?", "단순 이자 기준으로 연 500만 원, 월 약 41만 6천 원 수준입니다."),
                new FaqItem("원리금균등이면 월 이자만 내나요?", "아닙니다. 매달 원금 일부와 이자를 함께 상환합니다."),
                new FaqItem("만기일시상환은 왜 월 부담이 낮게 보이나요?", "원금은 만기까지 미루고 이자만 내기 때문입니다."),
                new FaqItem("대출기간이 길수록 무조건 좋은가요?", "월 납입액은 줄지만 총이자는 늘어나는 경우가 많습니다."),
                new FaqItem("비교에 필요한 계산기는 무엇인가요?", "대출이자 계산기와 대출 갈아타기 계산기를 함께 보면 유용합니다.")
        );
    }

    private List<FaqItem> repaymentDifferenceGuideFaqs() {
        return List.of(
                new FaqItem("원리금균등은 누가 많이 쓰나요?", "월 현금흐름을 안정적으로 관리하려는 직장인 대출에서 많이 사용됩니다."),
                new FaqItem("원금균등이 총이자가 적은 이유는 무엇인가요?", "원금을 더 빨리 줄여서 남은 원금에 붙는 이자가 빨리 감소하기 때문입니다."),
                new FaqItem("초기 부담이 큰 방식은 무엇인가요?", "원금균등이 일반적으로 첫 달 납입액이 가장 큽니다."),
                new FaqItem("만기일시상환은 위험한가요?", "만기 원금 상환 재원이 없다면 현금흐름 리스크가 큽니다."),
                new FaqItem("계산으로 바로 비교할 수 있나요?", "대출이자 계산기에서 같은 조건으로 상환방식만 바꿔 보면 차이가 바로 보입니다.")
        );
    }

    private List<FaqItem> ltvDsrGuideFaqs() {
        return List.of(
                new FaqItem("LTV는 무엇을 보나요?", "주택 가격 대비 대출 비율을 봅니다."),
                new FaqItem("DSR은 무엇을 보나요?", "소득 대비 전체 부채 상환 부담을 봅니다."),
                new FaqItem("둘 중 어느 것이 더 중요하나요?", "둘 다 중요하지만 실제 생활비 압박은 DSR 관점이 더 직접적일 수 있습니다."),
                new FaqItem("LTV가 낮으면 안전한가요?", "자기자본은 충분할 수 있지만 소득 대비 상환 부담은 여전히 높을 수 있습니다."),
                new FaqItem("어떤 계산기로 확인하나요?", "주담대 월납입 계산기에서 월 부담률을 함께 보는 것이 좋습니다.")
        );
    }

    private List<FaqItem> severanceAverageWageGuideFaqs() {
        return List.of(
                new FaqItem("평균임금은 왜 마지막 3개월을 보나요?", "퇴직 직전 임금 수준을 반영해 퇴직금 산정의 기준을 만들기 위해서입니다."),
                new FaqItem("상여금이 평균임금에 포함되나요?", "일정 범위의 상여금과 연차수당이 반영될 수 있어 실제 입력이 중요합니다."),
                new FaqItem("통상임금은 언제 쓰이나요?", "평균임금이 통상임금보다 낮을 때 하한 보정 기준으로 활용됩니다."),
                new FaqItem("퇴직금이 예상보다 적게 나온 이유는 무엇인가요?", "재직기간, 최근 3개월 임금 변화, 미포함 수당, 1년 미만 여부를 먼저 확인해야 합니다."),
                new FaqItem("어디서 확인해야 정확한가요?", "회사 급여자료와 취업규칙, 필요하면 노무사 검토까지 함께 보는 것이 가장 정확합니다.")
        );
    }

    private List<FaqItem> overseasDeductionGuideFaqs() {
        return List.of(
                new FaqItem("250만 원 공제는 종목마다 적용되나요?", "아닙니다. 연간 해외주식 양도차익 합계 기준으로 적용된다고 이해하는 것이 맞습니다."),
                new FaqItem("손실 종목이 있으면 상쇄되나요?", "같은 과세기간의 손익통산 여부를 함께 검토해야 합니다."),
                new FaqItem("250만 원 이하 수익이면 신고가 끝인가요?", "실제 신고 의무와 제출자료는 별도로 확인해야 합니다."),
                new FaqItem("환율은 왜 중요하나요?", "원화 환산 손익이 달라지기 때문에 세금 계산 결과에도 직접 영향을 줍니다."),
                new FaqItem("어떤 계산기를 같이 보면 좋나요?", "해외주식 세금 계산기와 배당 계산기를 함께 보는 것이 좋습니다.")
        );
    }

    private List<FaqItem> electricityTierGuideFaqs() {
        return List.of(
                new FaqItem("누진세는 무엇인가요?", "사용량이 많아질수록 더 높은 단가 구간이 적용되는 구조입니다."),
                new FaqItem("여름철 구간이 달라지는 이유는 무엇인가요?", "냉방 수요가 많아지는 시기라 가정용 부담을 완화하는 구조를 참고합니다."),
                new FaqItem("기본요금과 전력량요금은 어떻게 다른가요?", "기본요금은 구간별 고정 성격이고, 전력량요금은 실제 사용량에 비례합니다."),
                new FaqItem("부가세와 기금도 체감상 큰가요?", "사용량이 높아질수록 중간 합계가 커져 함께 올라갑니다."),
                new FaqItem("공식 청구서는 어디서 확인하나요?", "실제 납부 전에는 한전 또는 계약 사업자의 고지서를 확인해야 합니다.")
        );
    }

    private List<FaqItem> airconGuideFaqs() {
        return List.of(
                new FaqItem("하루 8시간 사용 기준은 현실적인가요?", "가정에서 자주 비교하는 기준이라 월 예상치 파악에 유용합니다."),
                new FaqItem("인버터와 정속형 차이가 큰가요?", "같은 정격 출력이라도 실제 소비전력 패턴이 달라 결과 차이가 날 수 있습니다."),
                new FaqItem("선풍기와 병행하면 절감되나요?", "체감온도 조절에 도움이 되어 실사용 시간과 설정온도를 낮추는 데 유리할 수 있습니다."),
                new FaqItem("대기전력까지 봐야 하나요?", "장기간 누적되면 무시하기 어려워 참고용으로 함께 보는 편이 좋습니다."),
                new FaqItem("계산 결과가 낮게 나오면 안심해도 되나요?", "가정 전체 사용량의 누진 구간 진입 여부까지 함께 봐야 합니다.")
        );
    }

    private List<FaqItem> carCostGuideFaqs() {
        return List.of(
                new FaqItem("유류비만 보면 충분한가요?", "아닙니다. 보험료, 세금, 주차비, 소모품 교체비가 누적되면 실제 부담이 크게 달라집니다."),
                new FaqItem("보험료는 왜 월평균으로 봐야 하나요?", "연납이라도 생활비 계획은 월 기준으로 세우는 편이 관리가 쉽기 때문입니다."),
                new FaqItem("km당 비용이 높은데 차를 바꾸는 게 맞나요?", "주행거리, 차량 가격, 대중교통 대체 가능성까지 함께 봐야 합니다."),
                new FaqItem("할부금도 유지비에 넣어야 하나요?", "현금흐름 관리 관점에서는 포함하는 것이 현실적입니다."),
                new FaqItem("전기차도 같은 방식으로 비교할 수 있나요?", "충전비와 보험료, 감가상각을 별도로 넣어 비교하는 방식이 유용합니다.")
        );
    }

    private List<FaqItem> dividend100GuideFaqs() {
        return List.of(
                new FaqItem("월 100만 원 배당이면 연간 얼마인가요?", "세전 기준 1,200만 원, 세후는 적용 세율에 따라 더 낮아집니다."),
                new FaqItem("필요한 투자금은 수익률마다 왜 다른가요?", "배당수익률이 높을수록 같은 현금흐름을 만드는 데 필요한 원금이 줄어들기 때문입니다."),
                new FaqItem("고배당이면 무조건 좋은가요?", "배당 지속 가능성, 주가 변동성, 세금까지 같이 봐야 합니다."),
                new FaqItem("월배당 ETF가 더 유리한가요?", "현금흐름 주기에는 유리할 수 있지만 총수익과 세후 기준은 별도로 비교해야 합니다."),
                new FaqItem("어떤 계산기가 필요하나요?", "배당 계산기에서 세전·세후를 먼저 계산하고 생활비 계산기와 연결해 보는 것이 좋습니다.")
        );
    }

    private List<FaqItem> budgetItemsGuideFaqs() {
        return List.of(
                new FaqItem("생활비 항목을 왜 고정비와 변동비로 나누나요?", "줄이기 쉬운 비용과 당장 줄이기 어려운 비용을 구분해야 예산 조정 우선순위가 보이기 때문입니다."),
                new FaqItem("구독 서비스도 꼭 적어야 하나요?", "소액이지만 누적되면 체감보다 큰 비중을 차지하는 경우가 많습니다."),
                new FaqItem("저축은 남는 돈으로만 하면 되나요?", "지출 후 남는 구조보다 선저축 구조가 예산 유지에 더 유리한 경우가 많습니다."),
                new FaqItem("생활비가 들쭉날쭉한 달은 어떻게 하나요?", "최근 3개월 평균과 최대치를 함께 적어 완충 구간을 만드는 것이 좋습니다."),
                new FaqItem("연결해서 볼 계산기는 무엇인가요?", "실수령액 계산기와 월 생활비 계산기를 함께 보는 흐름이 가장 실용적입니다.")
        );
    }

    private List<FaqItem> perFairValueLimitGuideFaqs() {
        return List.of(
                new FaqItem("PER 계산만으로 적정주가를 믿어도 되나요?", "권장하지 않습니다. 사업 구조와 이익의 질까지 함께 봐야 합니다."),
                new FaqItem("EPS가 왜곡되면 어떤 문제가 생기나요?", "적정주가 계산 자체가 크게 흔들릴 수 있습니다."),
                new FaqItem("업종마다 같은 PER을 써도 되나요?", "아닙니다. 성장성, 경기민감도, 자본집약도 차이가 큽니다."),
                new FaqItem("안전마진은 왜 필요한가요?", "가정이 틀릴 가능성을 감안해 보수적 가격을 보기 위해서입니다."),
                new FaqItem("어떤 계산기와 연결되나요?", "적정주가 계산기와 물타기 계산기를 함께 보면 좋습니다.")
        );
    }
}
