package com.example.moneytools.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityHeadersFilterTests {
    private final SecurityHeadersFilter filter = new SecurityHeadersFilter();

    @Test
    void addsBrowserSecurityHeaders() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest("GET", "/"), response, new MockFilterChain());

        assertThat(response.getHeader("Content-Security-Policy"))
                .contains("default-src 'self'")
                .contains("script-src 'self' 'nonce-")
                .doesNotContain("'unsafe-inline'");
        assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.getHeader("X-Frame-Options")).isEqualTo("DENY");
        assertThat(response.getHeader("Strict-Transport-Security")).isEqualTo("max-age=31536000; includeSubDomains");
        assertThat(response.getHeader("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin");
        assertThat(response.getHeader("Permissions-Policy")).contains("geolocation=()");
        assertThat(response.getHeader("Cross-Origin-Opener-Policy")).isEqualTo("same-origin");
        assertThat(response.getHeader("Cross-Origin-Resource-Policy")).isEqualTo("same-origin");
        assertThat(response.getHeader("Origin-Agent-Cluster")).isEqualTo("?1");
        assertThat(response.getHeader("X-Permitted-Cross-Domain-Policies")).isEqualTo("none");
        assertThat(response.getHeader("X-Download-Options")).isEqualTo("noopen");
    }

    @Test
    void exposesCspNonceToRequestForInlineStructuredData() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(request.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE)).isInstanceOf(String.class);
        assertThat((String) request.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE)).isNotBlank();
    }

    @Test
    void disablesCachingForPostResponses() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest("POST", "/salary-calculator"), response, new MockFilterChain());

        assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
        assertThat(response.getHeader("Pragma")).isEqualTo("no-cache");
    }
}
