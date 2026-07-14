package com.example.moneytools.controller;

import com.example.moneytools.adfit.AdFitProperties;
import com.example.moneytools.adfit.AdFitViewModel;
import com.example.moneytools.adfit.AdPlacement;
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


@ControllerAdvice
public class GlobalModelAdvice {
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
        CalculatorMeta meta = calculatorMeta(request);
        if (meta == null) {
            return new AdFitViewModel(adFitProperties.isV2Enabled(), adFitProperties.isPcSideEnabled(),
                    adFitProperties.isSecondaryEnabled(), "", "", "", "", "");
        }
        return new AdFitViewModel(
                adFitProperties.isV2Enabled(),
                adFitProperties.isPcSideEnabled(),
                adFitProperties.isSecondaryEnabled(),
                adFitProperties.unitFor(meta.group(), AdPlacement.RESULT_MOBILE),
                adFitProperties.unitFor(meta.group(), AdPlacement.RESULT_PC),
                adFitProperties.unitFor(meta.group(), AdPlacement.PC_SIDE),
                adFitProperties.unitFor(meta.group(), AdPlacement.SECONDARY_MOBILE),
                adFitProperties.unitFor(meta.group(), AdPlacement.SECONDARY_PC)
        );
    }

    @ModelAttribute("staticAssetVersion")
    public String staticAssetVersion() {
        return SitePages.sitemap().stream()
                .map(page -> page.lastModified().format(DateTimeFormatter.BASIC_ISO_DATE))
                .max(String::compareTo)
                .map(v -> v + "-moneycomma-v8")
                .orElse("20260425-moneycomma-v8");
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
