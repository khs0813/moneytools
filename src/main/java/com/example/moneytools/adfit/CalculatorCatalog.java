package com.example.moneytools.adfit;

import com.example.moneytools.seo.PageInfo;
import com.example.moneytools.seo.SitePages;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CalculatorCatalog {
    private static final Set<String> CALCULATOR_KEYS = Set.of(
            "dividend",
            "fair-value",
            "loan",
            "stock-average",
            "loan-refinance",
            "mortgage",
            "annual-salary-net",
            "salary",
            "severance",
            "annual-leave",
            "exchange",
            "electricity-bill",
            "air-conditioner-cost",
            "car-maintenance",
            "monthly-budget",
            "stock-tax",
            "domestic-stock-tax",
            "overseas-tax"
    );

    public static final List<CalculatorMeta> ALL = List.of(
            meta("dividend", MoneyCalculatorGroup.FINANCE, List.of("stock-tax", "overseas-tax", "fair-value"), ContentDepth.LONG),
            meta("fair-value", MoneyCalculatorGroup.FINANCE, List.of("stock-average", "dividend", "stock-tax"), ContentDepth.LONG),
            meta("loan", MoneyCalculatorGroup.FINANCE, List.of("loan-refinance", "mortgage"), ContentDepth.LONG),
            meta("stock-average", MoneyCalculatorGroup.FINANCE, List.of("fair-value", "stock-tax", "dividend"), ContentDepth.LONG),
            meta("loan-refinance", MoneyCalculatorGroup.FINANCE, List.of("loan", "mortgage"), ContentDepth.LONG),
            meta("mortgage", MoneyCalculatorGroup.FINANCE, List.of("loan", "loan-refinance"), ContentDepth.LONG),
            meta("annual-salary-net", MoneyCalculatorGroup.INCOME, List.of("salary", "severance", "annual-leave"), ContentDepth.LONG),
            meta("salary", MoneyCalculatorGroup.INCOME, List.of("annual-salary-net", "severance", "monthly-budget"), ContentDepth.LONG),
            meta("severance", MoneyCalculatorGroup.INCOME, List.of("salary", "annual-salary-net", "annual-leave"), ContentDepth.LONG),
            meta("annual-leave", MoneyCalculatorGroup.INCOME, List.of("salary", "severance"), ContentDepth.LONG),
            meta("exchange", MoneyCalculatorGroup.FINANCE, List.of("overseas-tax", "stock-tax"), ContentDepth.LONG),
            meta("electricity-bill", MoneyCalculatorGroup.LIVING, List.of("air-conditioner-cost", "monthly-budget"), ContentDepth.LONG),
            meta("air-conditioner-cost", MoneyCalculatorGroup.LIVING, List.of("electricity-bill", "monthly-budget"), ContentDepth.LONG),
            meta("car-maintenance", MoneyCalculatorGroup.LIVING, List.of("monthly-budget", "salary"), ContentDepth.LONG),
            meta("monthly-budget", MoneyCalculatorGroup.LIVING, List.of("salary", "car-maintenance", "electricity-bill"), ContentDepth.LONG),
            meta("stock-tax", MoneyCalculatorGroup.FINANCE, List.of("domestic-stock-tax", "overseas-tax", "dividend"), ContentDepth.LONG),
            meta("domestic-stock-tax", MoneyCalculatorGroup.FINANCE, List.of("stock-tax", "overseas-tax", "dividend"), ContentDepth.LONG),
            meta("overseas-tax", MoneyCalculatorGroup.FINANCE, List.of("stock-tax", "domestic-stock-tax", "exchange"), ContentDepth.LONG)
    );

    private static final Map<String, CalculatorMeta> BY_SLUG = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(CalculatorMeta::slug, Function.identity()));

    private CalculatorCatalog() {}

    public static Optional<CalculatorMeta> find(String slug) {
        return Optional.ofNullable(BY_SLUG.get(slug));
    }

    public static CalculatorMeta require(String slug) {
        return find(slug).orElseThrow(() -> new IllegalArgumentException("Unclassified calculator route: " + slug));
    }

    public static List<String> unclassifiedCalculatorKeys() {
        return SitePages.ALL.stream()
                .map(PageInfo::key)
                .filter(CALCULATOR_KEYS::contains)
                .filter(key -> !BY_SLUG.containsKey(key))
                .toList();
    }

    public static Set<String> calculatorKeys() {
        return CALCULATOR_KEYS;
    }

    private static CalculatorMeta meta(String slug, MoneyCalculatorGroup group, List<String> relatedSlugs, ContentDepth contentDepth) {
        return new CalculatorMeta(slug, SitePages.require(slug).label(), group, relatedSlugs, contentDepth);
    }
}
