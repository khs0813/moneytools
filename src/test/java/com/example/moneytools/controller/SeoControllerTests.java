package com.example.moneytools.controller;

import com.example.moneytools.config.AppProperties;
import com.example.moneytools.seo.PublicUrlService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SeoControllerTests {
    @Test
    void returnsEmptyAssetLinksUntilSigningFingerprintIsConfigured() {
        AppProperties appProperties = new AppProperties();
        SeoController controller = new SeoController(appProperties, new PublicUrlService(appProperties));

        assertThat(controller.assetLinks()).isEmpty();
    }

    @Test
    void returnsTrustedWebActivityAssetLinks() {
        AppProperties appProperties = new AppProperties();
        String fingerprint = "aa:bb:cc:dd:ee:ff:00:11:22:33:44:55:66:77:88:99:aa:bb:cc:dd:ee:ff:00:11:22:33:44:55:66:77:88:99";
        appProperties.getAndroid().setPackageName("com.moneycalculator.app");
        appProperties.getAndroid().setSha256CertFingerprints(List.of(fingerprint));
        SeoController controller = new SeoController(appProperties, new PublicUrlService(appProperties));

        List<Map<String, Object>> assetLinks = controller.assetLinks();

        assertThat(assetLinks).hasSize(1);
        assertThat(assetLinks.get(0)).containsEntry("relation", List.of("delegate_permission/common.handle_all_urls"));
        assertThat(assetLinks.get(0).get("target"))
                .isEqualTo(Map.of(
                        "namespace", "android_app",
                        "package_name", "com.moneycalculator.app",
                        "sha256_cert_fingerprints", List.of(fingerprint.toUpperCase())
                ));
    }
}
