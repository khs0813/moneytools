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
                new FaqItem("광고가 계산 버튼처럼 보이지 않나요?", "광고 영역은 콘텐츠와 구분되도록 설계되어 있으며, 사용자의 계산 흐름을 방해하지 않는 위치에 배치합니다.")
        ));
        model.addAttribute("calculatorPages", SitePages.navigation().stream()
                .filter(p -> !List.of("home", "guide", "privacy", "contact").contains(p.key()))
                .toList());
        return "index";
    }

    @GetMapping("/guide")
    public String guide(Model model) {
        PageInfo page = SitePages.require("guide");
        prepare(model, page, List.of(
                new FaqItem("이 사이트의 계산 결과를 공식 자료로 제출해도 되나요?", "아니요. 계산 결과는 참고용이며 공식 신고·증빙 자료가 아닙니다."),
                new FaqItem("2026년 기준은 어디까지 반영됐나요?", "4대보험 근로자 부담률, 국민연금 상·하한액, 현재 세율 구조, 해외주식 기본공제, 퇴직금·연차의 핵심 법정 계산 구조를 프로젝트에 반영했습니다.")
        ));
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

    @GetMapping("/privacy")
    public String privacy(Model model) {
        PageInfo page = SitePages.require("privacy");
        prepare(model, page, List.of());
        return "privacy-policy";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicyRedirect() {
        return "redirect:/privacy";
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
}
