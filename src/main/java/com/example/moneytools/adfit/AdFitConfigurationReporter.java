package com.example.moneytools.adfit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AdFitConfigurationReporter implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdFitConfigurationReporter.class);

    private final AdFitProperties properties;
    private final Environment environment;

    public AdFitConfigurationReporter(AdFitProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled() || productionProfileActive()) {
            return;
        }

        List<String> missing = List.of(
                missingKey(AdPlacement.CALCULATOR_POST_TOOL, "PUBLIC_ADFIT_CALC_POST_TOOL_DESKTOP", "PUBLIC_ADFIT_CALC_POST_TOOL_MOBILE"),
                missingKey(AdPlacement.CALCULATOR_ARTICLE_MID, "PUBLIC_ADFIT_CALC_ARTICLE_DESKTOP", "PUBLIC_ADFIT_CALC_ARTICLE_MOBILE"),
                missingKey(AdPlacement.GUIDE_ARTICLE_MID, "PUBLIC_ADFIT_GUIDE_DESKTOP", "PUBLIC_ADFIT_GUIDE_MOBILE"),
                missingKey(AdPlacement.HOME_MID, "PUBLIC_ADFIT_HOME_DESKTOP", "PUBLIC_ADFIT_HOME_MOBILE")
        ).stream().flatMap(List::stream).toList();

        if (!missing.isEmpty()) {
            log.warn("AdFit is enabled but these initial ad unit variables are missing: {}", String.join(", ", missing));
        }
    }

    private List<String> missingKey(AdPlacement placement, String desktopKey, String mobileKey) {
        AdFitSlotViewModel slot = properties.slotFor(placement);
        return List.of(
                slot.desktopUnit().isBlank() ? desktopKey : "",
                slot.mobileUnit().isBlank() ? mobileKey : ""
        ).stream().filter(value -> !value.isBlank()).toList();
    }

    private boolean productionProfileActive() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch("prod"::equalsIgnoreCase);
    }
}
