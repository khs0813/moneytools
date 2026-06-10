package com.example.moneytools.seo;

import com.example.moneytools.config.AppProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@Service
public class PublicUrlService {
    private static final String LOCAL_FALLBACK_BASE_URL = "http://localhost:8080";
    private static final String CANONICAL_BASE_URL = "https://www.moneycalculator.co.kr";
    private static final Set<String> TRUSTED_REQUEST_HOSTS = Set.of(
            "moneycalculator.co.kr",
            "www.moneycalculator.co.kr",
            "localhost",
            "127.0.0.1",
            "::1"
    );

    private final AppProperties appProperties;

    public PublicUrlService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String currentBaseUrl() {
        if (appProperties.hasConfiguredPublicBaseUrl()) {
            return appProperties.getBaseUrl();
        }
        try {
            String requestBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .build()
                    .toUriString();
            if (!isTrustedRequestBaseUrl(requestBaseUrl)) {
                return CANONICAL_BASE_URL;
            }
            if (isImplicitLocalhostRequest(requestBaseUrl)) {
                return CANONICAL_BASE_URL;
            }
            if (requestBaseUrl.endsWith("/")) {
                return requestBaseUrl.substring(0, requestBaseUrl.length() - 1);
            }
            return requestBaseUrl;
        } catch (IllegalStateException ex) {
            return LOCAL_FALLBACK_BASE_URL;
        }
    }

    public String absoluteUrl(String path) {
        return appProperties.buildUrl(currentBaseUrl(), path);
    }

    private boolean isTrustedRequestBaseUrl(String requestBaseUrl) {
        try {
            URI uri = new URI(requestBaseUrl);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && host != null
                    && TRUSTED_REQUEST_HOSTS.contains(host.toLowerCase());
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private boolean isImplicitLocalhostRequest(String requestBaseUrl) {
        try {
            URI uri = new URI(requestBaseUrl);
            String host = uri.getHost();
            return ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host))
                    && uri.getPort() == -1;
        } catch (URISyntaxException ex) {
            return true;
        }
    }
}
