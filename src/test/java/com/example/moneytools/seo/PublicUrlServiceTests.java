package com.example.moneytools.seo;

import com.example.moneytools.config.AppProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class PublicUrlServiceTests {
    @AfterEach
    void resetRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void ignoresUntrustedRequestHostWhenBaseUrlIsNotConfigured() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setScheme("https");
        request.setServerName("attacker.example");
        request.setServerPort(443);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PublicUrlService service = new PublicUrlService(new AppProperties());

        assertThat(service.absoluteUrl("/sitemap.xml"))
                .isEqualTo("https://www.moneycalculator.co.kr/sitemap.xml");
    }

    @Test
    void allowsLocalhostRequestHostForDevelopment() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PublicUrlService service = new PublicUrlService(new AppProperties());

        assertThat(service.absoluteUrl("/sitemap.xml"))
                .isEqualTo("http://localhost:8080/sitemap.xml");
    }
}
