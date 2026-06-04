package com.example.moneytools.controller;

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

    @ModelAttribute("adsenseEnabled")
    public boolean adsenseEnabled() { return appProperties.getAdsense().isEnabled(); }

    @ModelAttribute("adsenseClient")
    public String adsenseClient() { return appProperties.getAdsense().getClientId(); }

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

    @ModelAttribute("staticAssetVersion")
    public String staticAssetVersion() {
        return SitePages.sitemap().stream()
                .map(page -> page.lastModified().format(DateTimeFormatter.BASIC_ISO_DATE))
                .max(String::compareTo)
                .map(v -> v + "-moneycomma-v5")
                .orElse("20260425-moneycomma-v5");
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
