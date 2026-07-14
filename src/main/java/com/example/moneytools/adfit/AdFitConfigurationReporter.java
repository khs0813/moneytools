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
        if (!properties.isV2Enabled() || productionProfileActive()) {
            return;
        }

        List<String> missing = Arrays.stream(MoneyCalculatorGroup.values())
                .flatMap(group -> List.of(
                        missingKey(group, AdPlacement.RESULT_MOBILE),
                        missingKey(group, AdPlacement.RESULT_PC)
                ).stream())
                .filter(value -> !value.isBlank())
                .toList();

        if (!missing.isEmpty()) {
            log.warn("AdFit v2 is enabled but these result ad units are missing: {}", String.join(", ", missing));
        }
        if (properties.isPcSideEnabled() && properties.unitFor(MoneyCalculatorGroup.FINANCE, AdPlacement.PC_SIDE).isBlank()) {
            log.warn("AdFit PC side slot is enabled but NEXT_PUBLIC_ADFIT_MONEY_PC_SIDE is missing.");
        }
        if (properties.isSecondaryEnabled()) {
            if (properties.unitFor(MoneyCalculatorGroup.FINANCE, AdPlacement.SECONDARY_MOBILE).isBlank()) {
                log.warn("AdFit secondary mobile slot is enabled but NEXT_PUBLIC_ADFIT_MONEY_SECONDARY_MOBILE is missing.");
            }
            if (properties.unitFor(MoneyCalculatorGroup.FINANCE, AdPlacement.SECONDARY_PC).isBlank()) {
                log.warn("AdFit secondary PC slot is enabled but NEXT_PUBLIC_ADFIT_MONEY_SECONDARY_PC is missing.");
            }
        }
    }

    private boolean productionProfileActive() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch("prod"::equalsIgnoreCase);
    }

    private String missingKey(MoneyCalculatorGroup group, AdPlacement placement) {
        if (!properties.unitFor(group, placement).isBlank()) {
            return "";
        }
        return switch (group) {
            case FINANCE -> placement == AdPlacement.RESULT_MOBILE
                    ? "NEXT_PUBLIC_ADFIT_MONEY_FINANCE_RESULT_MOBILE"
                    : "NEXT_PUBLIC_ADFIT_MONEY_FINANCE_RESULT_PC";
            case INCOME -> placement == AdPlacement.RESULT_MOBILE
                    ? "NEXT_PUBLIC_ADFIT_MONEY_INCOME_RESULT_MOBILE"
                    : "NEXT_PUBLIC_ADFIT_MONEY_INCOME_RESULT_PC";
            case LIVING -> placement == AdPlacement.RESULT_MOBILE
                    ? "NEXT_PUBLIC_ADFIT_MONEY_LIVING_RESULT_MOBILE"
                    : "NEXT_PUBLIC_ADFIT_MONEY_LIVING_RESULT_PC";
        };
    }
}
