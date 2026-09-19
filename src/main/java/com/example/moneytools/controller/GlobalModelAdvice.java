package com.example.moneytools.controller;

import com.example.moneytools.adfit.CalculatorCatalog;
import com.example.moneytools.adfit.CalculatorMeta;
import com.example.moneytools.config.AppProperties;
import com.example.moneytools.config.SecurityHeadersFilter;
import com.example.moneytools.seo.PublicUrlService;
import com.example.moneytools.seo.SitePages;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;


@ControllerAdvice
public class GlobalModelAdvice {
    private static final Set<String> LONG_GUIDE_PATHS = Set.of(
            "/guide/aircon-8hours-cost",
            "/guide/car-monthly-cost",
            "/guide/dividend-100man",
            "/guide/dividend-tax",
            "/guide/electricity-tier",
            "/guide/loan-100m-interest",
            "/guide/monthly-budget-items",
            "/guide/overseas-stock-tax-deduction",
            "/guide/overseas-stock-tax",
            "/guide/repayment-method-difference",
            "/guide/salary-5000-net",
            "/guide/severance-average-wage",
            "/guide/stock-tax"
    );
    private static final Map<String, OgImage> PAGE_OG_IMAGES = Map.of(
            "/electricity-bill-calculator", new OgImage("/og/electricity-bill-calculator.png", "가정용 전기요금과 누진구간 계산 안내"),
            "/loan-interest-calculator", new OgImage("/og/loan-interest-calculator.png", "원리금균등 원금균등 월상환액 비교 안내"),
            "/annual-salary-net-calculator", new OgImage("/og/annual-salary-net-calculator.png", "2026 연봉 실수령액과 공제액 계산 안내"),
            "/air-conditioner-electricity-calculator", new OgImage("/og/air-conditioner-electricity-calculator.png", "하루 8시간 에어컨 예상 전기세 계산 안내"),
            "/domestic-stock-tax-calculator", new OgImage("/og/domestic-stock-tax-calculator.png", "국내주식 매도세금과 증권거래세 계산 안내"),
            "/stock-average-calculator", new OgImage("/og/stock-average-calculator.png", "추가매수 후 주식 평균단가 계산 안내")
    );
    private final AppProperties appProperties;
    private final PublicUrlService publicUrlService;

    public GlobalModelAdvice(AppProperties appProperties, PublicUrlService publicUrlService) {
        this.appProperties = appProperties;
        this.publicUrlService = publicUrlService;
    }

    @ModelAttribute("appName")
    public String appName() { return appProperties.getName(); }

    @ModelAttribute("appDescription")
    public String appDescription() { return appProperties.getDescription(); }

    @ModelAttribute("baseUrl")
    public String baseUrl() { return publicUrlService.currentBaseUrl(); }

    @ModelAttribute("contactEmail")
    public String contactEmail() { return appProperties.getContactEmail(); }

    @ModelAttribute("menus")
    public Object menus() { return SitePages.navigation(); }

    @ModelAttribute("currentYear")
    public int currentYear() { return Year.now().getValue(); }

    @ModelAttribute("adsAllowedOnPage")
    public boolean adsAllowedOnPage(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null
                && !uri.equals("/privacy")
                && !uri.equals("/privacy-policy")
                && !uri.equals("/terms")
                && !uri.equals("/disclaimer")
                && !uri.equals("/contact");
    }

    @ModelAttribute("calculatorMeta")
    public CalculatorMeta calculatorMeta(HttpServletRequest request) {
        return SitePages.ALL.stream()
                .filter(page -> page.path().equals(request.getRequestURI()))
                .map(page -> CalculatorCatalog.find(page.key()).orElse(null))
                .filter(meta -> meta != null)
                .findFirst()
                .orElse(null);
    }

    @ModelAttribute("staticAssetVersion")
    public String staticAssetVersion() {
        return SitePages.sitemap().stream()
                .map(page -> page.lastModified().format(DateTimeFormatter.BASIC_ISO_DATE))
                .max(String::compareTo)
                .map(v -> v + "-v1")
                .orElse("20260919-v1");
    }

    @ModelAttribute("defaultImageUrl")
    public String defaultImageUrl() { return publicUrlService.absoluteUrl("/og-image.png"); }

    @ModelAttribute("ogImageUrl")
    public String ogImageUrl(HttpServletRequest request) {
        OgImage image = PAGE_OG_IMAGES.get(request.getRequestURI());
        return publicUrlService.absoluteUrl(image == null ? "/og-image.png" : image.path());
    }

    @ModelAttribute("ogImageAlt")
    public String ogImageAlt(HttpServletRequest request) {
        OgImage image = PAGE_OG_IMAGES.get(request.getRequestURI());
        return image == null ? "머니계산기 금융 계산기 모음" : image.alt();
    }

    @ModelAttribute("googleSiteVerification")
    public String googleSiteVerification() { return appProperties.getGoogleSiteVerification(); }

    @ModelAttribute("naverSiteVerification")
    public String naverSiteVerification() { return appProperties.getNaverSiteVerification(); }

    @ModelAttribute("cspNonce")
    public String cspNonce(HttpServletRequest request) {
        Object nonce = request.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE);
        return nonce instanceof String value ? value : "";
    }

    private record OgImage(String path, String alt) {}
}
