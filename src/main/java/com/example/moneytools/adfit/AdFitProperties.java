package com.example.moneytools.adfit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "app.adfit")
public class AdFitProperties {
    private boolean enabled = false;
    private String allowedHosts = "www.moneycalculator.co.kr";
    private String experiment = "off";
    private String calcPreFaqRoutes = "";
    private final Placements placements = new Placements();
    private final Units units = new Units();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAllowedHosts() {
        return allowedHosts;
    }

    public void setAllowedHosts(String allowedHosts) {
        this.allowedHosts = allowedHosts;
    }

    public String getExperiment() {
        return experiment;
    }

    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }

    public String getCalcPreFaqRoutes() {
        return calcPreFaqRoutes;
    }

    public void setCalcPreFaqRoutes(String calcPreFaqRoutes) {
        this.calcPreFaqRoutes = calcPreFaqRoutes;
    }

    public Units getUnits() {
        return units;
    }

    public Placements getPlacements() {
        return placements;
    }

    public boolean isAllowedHost(String host) {
        if (!StringUtils.hasText(host)) {
            return false;
        }
        return splitHosts(allowedHosts).contains(host.trim().toLowerCase());
    }

    public boolean isCalcPreFaqAllowed(String path) {
        return splitPaths(calcPreFaqRoutes).contains(normalizePath(path));
    }

    public AdFitSlotViewModel slotFor(AdPlacement placement) {
        if (!isPlacementEnabled(placement)) {
            return AdFitSlotViewModel.empty(placement.key());
        }
        return switch (placement) {
            case CALCULATOR_POST_TOOL -> calculatorPostToolSlot();
            case CALCULATOR_ARTICLE_MID -> new AdFitSlotViewModel(
                    placement.key(),
                    normalize(units.calcArticleDesktop), 728, 90,
                    normalize(units.calcArticleMobile), 320, 100);
            case CALCULATOR_PRE_FAQ -> new AdFitSlotViewModel(
                    placement.key(),
                    normalize(units.calcPreFaqDesktop), 728, 90,
                    normalize(units.calcPreFaqMobile), 320, 100);
            case GUIDE_ARTICLE_MID, GUIDE_PRE_FAQ, GUIDE_INDEX -> new AdFitSlotViewModel(
                    placement.key(),
                    normalize(units.guideDesktop), 728, 90,
                    normalize(units.guideMobile), 320, 100);
            case HOME_MID -> new AdFitSlotViewModel(
                    placement.key(),
                    normalize(units.homeDesktop), 728, 90,
                    normalize(units.homeMobile), 320, 100);
        };
    }

    public boolean isPlacementEnabled(AdPlacement placement) {
        return switch (placement) {
            case CALCULATOR_POST_TOOL -> placements.calculatorPostTool;
            case CALCULATOR_ARTICLE_MID -> placements.calculatorArticleMid;
            case CALCULATOR_PRE_FAQ -> placements.calculatorPreFaq;
            case GUIDE_ARTICLE_MID -> placements.guideArticleMid;
            case GUIDE_PRE_FAQ -> placements.guidePreFaq;
            case GUIDE_INDEX -> placements.guideIndex;
            case HOME_MID -> placements.homeMid;
        };
    }

    public Set<String> configuredKeysFor(AdPlacement placement) {
        AdFitSlotViewModel slot = slotFor(placement);
        return Arrays.stream(new String[] {slot.desktopUnit(), slot.mobileUnit()})
                .filter(StringUtils::hasText)
                .collect(Collectors.toUnmodifiableSet());
    }

    private AdFitSlotViewModel calculatorPostToolSlot() {
        boolean useMobileAlt = "calc_post_tool_mobile_alt".equalsIgnoreCase(normalize(experiment))
                && StringUtils.hasText(units.calcPostToolMobileAlt);
        return new AdFitSlotViewModel(
                AdPlacement.CALCULATOR_POST_TOOL.key(),
                normalize(units.calcPostToolDesktop), 300, 250,
                useMobileAlt ? normalize(units.calcPostToolMobileAlt) : normalize(units.calcPostToolMobile),
                useMobileAlt ? 300 : 320,
                useMobileAlt ? 250 : 100);
    }

    private Set<String> splitHosts(String value) {
        if (!StringUtils.hasText(value)) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(this::normalize)
                .map(String::toLowerCase)
                .filter(StringUtils::hasText)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<String> splitPaths(String value) {
        if (!StringUtils.hasText(value)) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(this::normalizePath)
                .filter(StringUtils::hasText)
                .collect(Collectors.toUnmodifiableSet());
    }

    private String normalizePath(String value) {
        String normalized = normalize(value).toLowerCase();
        if (!StringUtils.hasText(normalized)) {
            return "";
        }
        if (normalized.contains("://")) {
            return normalized;
        }
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    public static class Placements {
        private boolean calculatorPostTool = true;
        private boolean calculatorArticleMid = true;
        private boolean calculatorPreFaq = true;
        private boolean guideArticleMid = true;
        private boolean guidePreFaq = true;
        private boolean guideIndex = true;
        private boolean homeMid = true;

        public boolean isCalculatorPostTool() { return calculatorPostTool; }
        public void setCalculatorPostTool(boolean calculatorPostTool) { this.calculatorPostTool = calculatorPostTool; }
        public boolean isCalculatorArticleMid() { return calculatorArticleMid; }
        public void setCalculatorArticleMid(boolean calculatorArticleMid) { this.calculatorArticleMid = calculatorArticleMid; }
        public boolean isCalculatorPreFaq() { return calculatorPreFaq; }
        public void setCalculatorPreFaq(boolean calculatorPreFaq) { this.calculatorPreFaq = calculatorPreFaq; }
        public boolean isGuideArticleMid() { return guideArticleMid; }
        public void setGuideArticleMid(boolean guideArticleMid) { this.guideArticleMid = guideArticleMid; }
        public boolean isGuidePreFaq() { return guidePreFaq; }
        public void setGuidePreFaq(boolean guidePreFaq) { this.guidePreFaq = guidePreFaq; }
        public boolean isGuideIndex() { return guideIndex; }
        public void setGuideIndex(boolean guideIndex) { this.guideIndex = guideIndex; }
        public boolean isHomeMid() { return homeMid; }
        public void setHomeMid(boolean homeMid) { this.homeMid = homeMid; }
    }

    public static class Units {
        private String calcPostToolDesktop = "";
        private String calcPostToolMobile = "";
        private String calcArticleDesktop = "";
        private String calcArticleMobile = "";
        private String guideDesktop = "";
        private String guideMobile = "";
        private String homeDesktop = "";
        private String homeMobile = "";
        private String calcPostToolMobileAlt = "";
        private String calcPreFaqDesktop = "";
        private String calcPreFaqMobile = "";

        public String getCalcPostToolDesktop() { return calcPostToolDesktop; }
        public void setCalcPostToolDesktop(String calcPostToolDesktop) { this.calcPostToolDesktop = calcPostToolDesktop; }
        public String getCalcPostToolMobile() { return calcPostToolMobile; }
        public void setCalcPostToolMobile(String calcPostToolMobile) { this.calcPostToolMobile = calcPostToolMobile; }
        public String getCalcArticleDesktop() { return calcArticleDesktop; }
        public void setCalcArticleDesktop(String calcArticleDesktop) { this.calcArticleDesktop = calcArticleDesktop; }
        public String getCalcArticleMobile() { return calcArticleMobile; }
        public void setCalcArticleMobile(String calcArticleMobile) { this.calcArticleMobile = calcArticleMobile; }
        public String getGuideDesktop() { return guideDesktop; }
        public void setGuideDesktop(String guideDesktop) { this.guideDesktop = guideDesktop; }
        public String getGuideMobile() { return guideMobile; }
        public void setGuideMobile(String guideMobile) { this.guideMobile = guideMobile; }
        public String getHomeDesktop() { return homeDesktop; }
        public void setHomeDesktop(String homeDesktop) { this.homeDesktop = homeDesktop; }
        public String getHomeMobile() { return homeMobile; }
        public void setHomeMobile(String homeMobile) { this.homeMobile = homeMobile; }
        public String getCalcPostToolMobileAlt() { return calcPostToolMobileAlt; }
        public void setCalcPostToolMobileAlt(String calcPostToolMobileAlt) { this.calcPostToolMobileAlt = calcPostToolMobileAlt; }
        public String getCalcPreFaqDesktop() { return calcPreFaqDesktop; }
        public void setCalcPreFaqDesktop(String calcPreFaqDesktop) { this.calcPreFaqDesktop = calcPreFaqDesktop; }
        public String getCalcPreFaqMobile() { return calcPreFaqMobile; }
        public void setCalcPreFaqMobile(String calcPreFaqMobile) { this.calcPreFaqMobile = calcPreFaqMobile; }
    }
}
