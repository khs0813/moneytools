package com.example.moneytools.adfit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdFitViewModelTests {
    @Test
    void blocksAdsWhenHostIsNotAllowed() {
        AdFitProperties properties = enabledProperties();
        AdFitViewModel viewModel = new AdFitViewModel(
                properties,
                "/salary-calculator",
                "localhost",
                AdFitViewModel.PageKind.CALCULATOR);

        assertThat(viewModel.enabledForPage()).isFalse();
        assertThat(viewModel.hasRenderablePageSlot()).isFalse();
    }

    @Test
    void exposesCalculatorPostToolSlotWhenConfiguredForAllowedHost() {
        AdFitProperties properties = enabledProperties();
        AdFitViewModel viewModel = new AdFitViewModel(
                properties,
                "/salary-calculator",
                "www.moneycalculator.co.kr",
                AdFitViewModel.PageKind.CALCULATOR);

        AdFitSlotViewModel slot = viewModel.slot("calculator_post_tool");

        assertThat(slot.renderable()).isTrue();
        assertThat(slot.desktopWidth()).isEqualTo(300);
        assertThat(slot.desktopHeight()).isEqualTo(250);
        assertThat(slot.mobileWidth()).isEqualTo(320);
        assertThat(slot.mobileHeight()).isEqualTo(100);
    }

    @Test
    void shortGuidesDoNotRenderPreFaqSlot() {
        AdFitProperties properties = enabledProperties();
        AdFitViewModel viewModel = new AdFitViewModel(
                properties,
                "/guide/salary-3000-net",
                "www.moneycalculator.co.kr",
                AdFitViewModel.PageKind.GUIDE_ARTICLE_SHORT);

        assertThat(viewModel.slot("guide_article_mid").renderable()).isTrue();
        assertThat(viewModel.slot("guide_pre_faq").renderable()).isFalse();
    }

    @Test
    void placementFlagCanDisableOneSlotWithoutClearingAdUnitConfiguration() {
        AdFitProperties properties = enabledProperties();
        properties.getPlacements().setCalculatorArticleMid(false);
        AdFitViewModel viewModel = new AdFitViewModel(
                properties,
                "/salary-calculator",
                "www.moneycalculator.co.kr",
                AdFitViewModel.PageKind.CALCULATOR);

        assertThat(viewModel.slot("calculator_article_mid").renderable()).isFalse();
        assertThat(viewModel.slot("calculator_post_tool").renderable()).isTrue();
    }

    @Test
    void offExperimentReportsControlForAnalytics() {
        AdFitProperties properties = enabledProperties();
        AdFitViewModel viewModel = new AdFitViewModel(
                properties,
                "/salary-calculator",
                "www.moneycalculator.co.kr",
                AdFitViewModel.PageKind.CALCULATOR);

        assertThat(viewModel.experimentId()).isEqualTo("control");
    }

    private AdFitProperties enabledProperties() {
        AdFitProperties properties = new AdFitProperties();
        properties.setEnabled(true);
        properties.getUnits().setCalcPostToolDesktop("desktop-unit");
        properties.getUnits().setCalcPostToolMobile("mobile-unit");
        properties.getUnits().setCalcArticleDesktop("article-desktop-unit");
        properties.getUnits().setCalcArticleMobile("article-mobile-unit");
        properties.getUnits().setGuideDesktop("guide-desktop-unit");
        properties.getUnits().setGuideMobile("guide-mobile-unit");
        properties.getUnits().setHomeDesktop("home-desktop-unit");
        properties.getUnits().setHomeMobile("home-mobile-unit");
        return properties;
    }
}
