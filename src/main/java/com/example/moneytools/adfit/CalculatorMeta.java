package com.example.moneytools.adfit;

import java.util.List;

public record CalculatorMeta(
        String slug,
        String title,
        MoneyCalculatorGroup group,
        List<String> relatedSlugs,
        ContentDepth contentDepth
) {}
