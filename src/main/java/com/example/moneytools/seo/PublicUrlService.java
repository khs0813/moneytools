package com.example.moneytools.seo;

import com.example.moneytools.config.AppProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class PublicUrlService {
    private static final String LOCAL_FALLBACK_BASE_URL = "http://localhost:8080";

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
}
