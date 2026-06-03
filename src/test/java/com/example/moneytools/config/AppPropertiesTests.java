package com.example.moneytools.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppPropertiesTests {
    @Test
    void rejectsUnsafeBaseUrlSchemes() {
        AppProperties properties = new AppProperties();
        properties.setBaseUrl("javascript:alert(1)");

        assertThat(properties.hasConfiguredPublicBaseUrl()).isFalse();
        assertThat(properties.url("/sitemap.xml")).isEqualTo("/sitemap.xml");
    }

    @Test
    void rejectsBaseUrlWithControlCharacters() {
        AppProperties properties = new AppProperties();
        properties.setBaseUrl("https://www.moneycalculator.co.kr\r\nX-Test: injected");

        assertThat(properties.hasConfiguredPublicBaseUrl()).isFalse();
        assertThat(properties.url("/")).isEqualTo("/");
    }

    @Test
    void buildsUrlFromValidHttpsOrigin() {
        AppProperties properties = new AppProperties();
        properties.setBaseUrl("https://www.moneycalculator.co.kr/");

        assertThat(properties.url("/salary-calculator"))
                .isEqualTo("https://www.moneycalculator.co.kr/salary-calculator");
    }

    @Test
    void fallsBackWhenContactEmailIsUnsafe() {
        AppProperties properties = new AppProperties();
        properties.setContactEmail("test@example.com?subject=<script>");

        assertThat(properties.getContactEmail()).isEqualTo("moneyfinancecalculator@gmail.com");
    }
}
