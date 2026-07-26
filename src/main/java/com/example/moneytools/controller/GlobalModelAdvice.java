package com.example.moneytools.controller;

import com.example.moneytools.adfit.AdFitProperties;
import com.example.moneytools.adfit.AdFitViewModel;
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
    private final AppProperties appProperties;
    private final AdFitProperties adFitProperties;
    private final PublicUrlService publicUrlService;

    public GlobalModelAdvice(AppProperties appProperties, AdFitProperties adFitProperties, PublicUrlService publicUrlService) {
        this.appProperties = appProperties;
        this.adFitProperties = adFitProperties;
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

    @ModelAttribute("adfit")
    public AdFitViewModel adfit(HttpServletRequest request) {
        String path = request.getRequestURI();
        return new AdFitViewModel(adFitProperties, path, request.getServerName(), adFitPageKind(path));
    }

    private AdFitViewModel.PageKind adFitPageKind(String path) {
        if (path == null
                || path.equals("/about")
                || path.equals("/privacy")
                || path.equals("/privacy-policy")
                || path.equals("/terms")
                || path.equals("/disclaimer")
                || path.equals("/contact")
                || path.startsWith("/error")
                || path.endsWith(".xml")
                || path.endsWith(".json")
                || path.endsWith(".rss")) {
            return AdFitViewModel.PageKind.BLOCKED;
        }
        if (path.equals("/")) {
            return AdFitViewModel.PageKind.HOME;
        }
        if (path.equals("/guide")) {
            return AdFitViewModel.PageKind.GUIDE_INDEX;
        }
        if (path.startsWith("/guide/")) {
            return LONG_GUIDE_PATHS.contains(path)
                    ? AdFitViewModel.PageKind.GUIDE_ARTICLE_LONG
                    : AdFitViewModel.PageKind.GUIDE_ARTICLE_SHORT;
        }
        return SitePages.ALL.stream()
                .filter(page -> page.path().equals(path))
                .map(page -> CalculatorCatalog.calculatorKeys().contains(page.key())
                        ? AdFitViewModel.PageKind.CALCULATOR
                        : AdFitViewModel.PageKind.BLOCKED)
                .findFirst()
                .orElse(AdFitViewModel.PageKind.BLOCKED);
    }

    @ModelAttribute("staticAssetVersion")
    public String staticAssetVersion() {
        return SitePages.sitemap().stream()
                .map(page -> page.lastModified().format(DateTimeFormatter.BASIC_ISO_DATE))
                .max(String::compareTo)
                .map(v -> v + "-moneycomma-v9")
                .orElse("20260425-moneycomma-v9");
    }

    @ModelAttribute("defaultImageUrl")
    public String defaultImageUrl() { return publicUrlService.absoluteUrl("/og-image.png"); }

    @ModelAttribute("googleSiteVerification")
    public String googleSiteVerification() { return appProperties.getGoogleSiteVerification(); }

    @ModelAttribute("naverSiteVerification")
    public String naverSiteVerification() { return appProperties.getNaverSiteVerification(); }

    @ModelAttribute("cspNonce")
    public String cspNonce(HttpServletRequest request) {
        Object nonce = request.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE);
        return nonce instanceof String value ? value : "";
    }
}
