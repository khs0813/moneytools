package com.example.moneytools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private static final Pattern SAFE_EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");

    private String name = "머니계산기";
    private String baseUrl = "";
    private String description = "금융 계산기 모음";
    private String contactEmail = "moneyfinancecalculator@gmail.com";
    private String googleSiteVerification = "";
    private String naverSiteVerification = "";

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBaseUrl() { return sanitizeBaseUrl(baseUrl); }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactEmail() {
        String trimmed = contactEmail == null ? "" : contactEmail.trim();
        return SAFE_EMAIL.matcher(trimmed).matches() ? trimmed : "moneyfinancecalculator@gmail.com";
    }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getGoogleSiteVerification() { return googleSiteVerification; }
    public void setGoogleSiteVerification(String googleSiteVerification) { this.googleSiteVerification = googleSiteVerification; }

    public String getNaverSiteVerification() { return naverSiteVerification; }
    public void setNaverSiteVerification(String naverSiteVerification) { this.naverSiteVerification = naverSiteVerification; }

    public boolean hasConfiguredPublicBaseUrl() {
        String normalized = getBaseUrl();
        if (!StringUtils.hasText(normalized)) {
            return false;
        }
        String lower = normalized.toLowerCase();
        return !(lower.contains("example.com") || lower.contains("your-domain") || lower.contains("localhost") || lower.contains("127.0.0.1"));
    }

    public String buildUrl(String base, String path) {
        String normalizedBase = sanitizeBaseUrl(base);
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

    private String sanitizeBaseUrl(String value) {
        String trimmed = trimTrailingSlash(value);
        if (!StringUtils.hasText(trimmed) || containsControlCharacter(trimmed)) {
            return "";
        }
        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            if (!("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                return "";
            }
            if (!StringUtils.hasText(uri.getHost()) || uri.getRawUserInfo() != null
                    || uri.getRawQuery() != null || uri.getRawFragment() != null) {
                return "";
            }
            if ("moneycalculator.co.kr".equalsIgnoreCase(uri.getHost())) {
                URI canonicalUri = new URI(uri.getScheme(), uri.getUserInfo(), "www.moneycalculator.co.kr", uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
                return canonicalUri.toString();
            }
            return uri.toString();
        } catch (URISyntaxException ex) {
            return "";
        }
    }

    private boolean containsControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private String normalizePath(String path) {
        if (!StringUtils.hasText(path) || "/".equals(path)) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

}
