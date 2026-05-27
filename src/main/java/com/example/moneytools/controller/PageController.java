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
                new FaqItem("주식 매도 세금 계산은 어디서 하나요?", "국내주식은 국내 주식 세금 계산기에서 매도금액 기준 증권거래세를 확인하고, 해외주식은 해외주식 세금 계산기에서 양도소득세와 배당세를 함께 확인할 수 있습니다.")
        ));
        return "stock-tax-calculator";
    }

    @GetMapping("/guide/stock-tax")
    public String stockTaxGuide(Model model) {
        PageInfo page = SitePages.require("guide-stock-tax");
        prepare(model, page, List.of());
        return "guide-stock-tax";
    }

    @GetMapping("/guide/overseas-stock-tax")
    public String overseasStockTaxGuide(Model model) {
        PageInfo page = SitePages.require("guide-overseas-stock-tax");
        prepare(model, page, List.of());
        return "guide-overseas-stock-tax";
    }

    @GetMapping("/guide/dividend-tax")
    public String dividendTaxGuide(Model model) {
        PageInfo page = SitePages.require("guide-dividend-tax");
        prepare(model, page, List.of());
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
}
