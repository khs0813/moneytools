package com.example.moneytools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name = "머니계산기";
    private String baseUrl = "";
    private String description = "금융 계산기 모음";
    private String contactEmail = "contact@example.com";
    private String googleSiteVerification = "";
    private String naverSiteVerification = "";
    private Adsense adsense = new Adsense();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBaseUrl() { return trimTrailingSlash(baseUrl); }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getGoogleSiteVerification() { return googleSiteVerification; }
    public void setGoogleSiteVerification(String googleSiteVerification) { this.googleSiteVerification = googleSiteVerification; }

    public String getNaverSiteVerification() { return naverSiteVerification; }
    public void setNaverSiteVerification(String naverSiteVerification) { this.naverSiteVerification = naverSiteVerification; }

    public Adsense getAdsense() { return adsense; }
    public void setAdsense(Adsense adsense) { this.adsense = adsense; }

    public boolean hasConfiguredPublicBaseUrl() {
        String normalized = getBaseUrl();
        if (!StringUtils.hasText(normalized)) {
            return false;
        }
        String lower = normalized.toLowerCase();
        return !(lower.contains("example.com") || lower.contains("your-domain") || lower.contains("localhost") || lower.contains("127.0.0.1"));
    }

    public String buildUrl(String base, String path) {
        String normalizedBase = trimTrailingSlash(base);
        if (!StringUtils.hasText(normalizedBase)) {
            return normalizePath(path);
        }
        if (!StringUtils.hasText(path) || "/".equals(path)) {
            return normalizedBase + "/";
        }
        return normalizedBase + normalizePath(path);
    }

    public String url(String path) {
        return buildUrl(getBaseUrl(), path);
    }

    private String trimTrailingSlash(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    private String normalizePath(String path) {
        if (!StringUtils.hasText(path) || "/".equals(path)) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    public static class Adsense {
        private boolean enabled = false;
        private String clientId = "ca-pub-0000000000000000";
        private Map<String, String> slots = new LinkedHashMap<>();

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }

        public Map<String, String> getSlots() { return slots; }
        public void setSlots(Map<String, String> slots) { this.slots = slots; }

        public String slot(String key) {
            return slots.getOrDefault(key, "0000000000");
        }
    }
}
