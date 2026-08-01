package com.example.moneytools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private static final Pattern SAFE_EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");
    private static final Pattern SAFE_ANDROID_PACKAGE = Pattern.compile("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$");
    private static final Pattern SAFE_SHA256_FINGERPRINT = Pattern.compile("^([0-9A-Fa-f]{2}:){31}[0-9A-Fa-f]{2}$");

    private String name = "머니계산기";
    private String baseUrl = "";
    private String description = "금융 계산기 모음";
    private String contactEmail = "moneyfinancecalculator@gmail.com";
    private String googleSiteVerification = "";
    private String naverSiteVerification = "";
    private Adsense adsense = new Adsense();
    private Android android = new Android();

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

    public Adsense getAdsense() { return adsense; }
    public void setAdsense(Adsense adsense) { this.adsense = adsense; }

    public Android getAndroid() { return android; }
    public void setAndroid(Android android) { this.android = android == null ? new Android() : android; }

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

    public static class Adsense {
        private boolean enabled = false;
        private String clientId = "ca-pub-0000000000000000";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
    }

    public static class Android {
        private String packageName = "com.moneycalculator.app";
        private List<String> sha256CertFingerprints = new ArrayList<>();

        public String getPackageName() {
            String trimmed = packageName == null ? "" : packageName.trim();
            return SAFE_ANDROID_PACKAGE.matcher(trimmed).matches() ? trimmed : "com.moneycalculator.app";
        }

        public void setPackageName(String packageName) { this.packageName = packageName; }

        public List<String> getSha256CertFingerprints() {
            if (sha256CertFingerprints == null) {
                return List.of();
            }
            return sha256CertFingerprints.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .filter(fingerprint -> SAFE_SHA256_FINGERPRINT.matcher(fingerprint).matches())
                    .map(fingerprint -> fingerprint.toUpperCase(Locale.ROOT))
                    .distinct()
                    .toList();
        }

        public void setSha256CertFingerprints(List<String> sha256CertFingerprints) {
            this.sha256CertFingerprints = sha256CertFingerprints == null ? new ArrayList<>() : new ArrayList<>(sha256CertFingerprints);
        }
    }
}
