package com.example.moneytools.seo;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CanonicalDomainRedirectFilterTests {
    private final CanonicalDomainRedirectFilter filter = new CanonicalDomainRedirectFilter();

    @Test
    void redirectsApexDomainToWwwHttps() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/salary-calculator");
        request.addHeader("Host", "moneycalculator.co.kr");
        request.setQueryString("amount=3000000");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(301);
        assertThat(response.getHeader("Location"))
                .isEqualTo("https://www.moneycalculator.co.kr/salary-calculator?amount=3000000");
    }

    @Test
    void redirectsHttpWwwDomainToHttps() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/sitemap.xml");
        request.addHeader("Host", "www.moneycalculator.co.kr");
        request.addHeader("X-Forwarded-Proto", "http");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(301);
        assertThat(response.getHeader("Location")).isEqualTo("https://www.moneycalculator.co.kr/sitemap.xml");
    }

    @Test
    void encodesRedirectLocationQueryString() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/salary-calculator");
        request.addHeader("Host", "moneycalculator.co.kr");
        request.setQueryString("amount=<script>alert(1)</script>");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(301);
        assertThat(response.getHeader("Location"))
                .isEqualTo("https://www.moneycalculator.co.kr/salary-calculator?amount=%3Cscript%3Ealert(1)%3C/script%3E");
    }

    @Test
    void allowsHttpsWwwDomain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.addHeader("Host", "www.moneycalculator.co.kr");
        request.addHeader("X-Forwarded-Proto", "https");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("Location")).isNull();
    }

    @Test
    void allowsLocalDevelopmentHosts() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.addHeader("Host", "localhost:8080");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("Location")).isNull();
    }
}
