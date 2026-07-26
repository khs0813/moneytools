package com.example.moneytools.config;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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

    @Test
    void homeFeatureUsesConfiguredClusterAndDateWindow() {
        AppProperties properties = new AppProperties();
        properties.getHome().setFeaturedCluster("electricity");
        properties.getHome().setActiveFrom(LocalDate.of(2026, 6, 1));
        properties.getHome().setActiveUntil(LocalDate.of(2026, 9, 30));

        assertThat(properties.getHome().isActive("electricity", LocalDate.of(2026, 7, 26))).isTrue();
        assertThat(properties.getHome().isActive("loan", LocalDate.of(2026, 7, 26))).isFalse();
        assertThat(properties.getHome().isActive("electricity", LocalDate.of(2026, 10, 1))).isFalse();
    }
}
