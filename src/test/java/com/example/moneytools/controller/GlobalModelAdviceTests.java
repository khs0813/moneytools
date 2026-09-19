package com.example.moneytools.controller;

import com.example.moneytools.config.AppProperties;
import com.example.moneytools.seo.PublicUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalModelAdviceTests {
    private final GlobalModelAdvice advice =
            new GlobalModelAdvice(new AppProperties(), new PublicUrlService(new AppProperties()));

    @Test
    void disablesAdsOnPolicyAndContactPages() {
        assertThat(advice.adsAllowedOnPage(new MockHttpServletRequest("GET", "/privacy-policy"))).isFalse();
        assertThat(advice.adsAllowedOnPage(new MockHttpServletRequest("GET", "/terms"))).isFalse();
        assertThat(advice.adsAllowedOnPage(new MockHttpServletRequest("GET", "/disclaimer"))).isFalse();
        assertThat(advice.adsAllowedOnPage(new MockHttpServletRequest("GET", "/contact"))).isFalse();
    }

    @Test
    void allowsAdsOnCalculatorPages() {
        assertThat(advice.adsAllowedOnPage(new MockHttpServletRequest("GET", "/salary-calculator"))).isTrue();
    }

    @Test
    void usesPageSpecificOgImageForNaverSeoTargets() {
        assertThat(advice.ogImageUrl(new MockHttpServletRequest("GET", "/loan-interest-calculator")))
                .endsWith("/og/loan-interest-calculator.png");
        assertThat(advice.ogImageAlt(new MockHttpServletRequest("GET", "/loan-interest-calculator")))
                .contains("월상환액");
    }

    @Test
    void fallsBackToDefaultOgImage() {
        assertThat(advice.ogImageUrl(new MockHttpServletRequest("GET", "/about")))
                .endsWith("/og-image.png");
    }
}
