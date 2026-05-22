package com.example.moneytools.controller;

import com.example.moneytools.config.AppProperties;
import com.example.moneytools.seo.PublicUrlService;
import com.example.moneytools.seo.SitePages;
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

    @ModelAttribute("adSlotTop")
    public String adSlotTop() { return appProperties.getAdsense().slot("top"); }

    @ModelAttribute("adSlotContent")
    public String adSlotContent() { return appProperties.getAdsense().slot("content"); }

    @ModelAttribute("adSlotResult")
    public String adSlotResult() { return appProperties.getAdsense().slot("result"); }

    @ModelAttribute("adSlotSidebar")
    public String adSlotSidebar() { return appProperties.getAdsense().slot("sidebar"); }

    @ModelAttribute("staticAssetVersion")
    public String staticAssetVersion() {
        return SitePages.sitemap().stream()
                .map(page -> page.lastModified().format(DateTimeFormatter.BASIC_ISO_DATE))
                .max(String::compareTo)
                .map(v -> v + "-moneycomma-v3")
                .orElse("20260425-moneycomma-v3");
    }

    @ModelAttribute("defaultImageUrl")
    public String defaultImageUrl() { return publicUrlService.absoluteUrl("/img/og-default.svg"); }

    @ModelAttribute("googleSiteVerification")
    public String googleSiteVerification() { return appProperties.getGoogleSiteVerification(); }

    @ModelAttribute("naverSiteVerification")
    public String naverSiteVerification() { return appProperties.getNaverSiteVerification(); }
}
