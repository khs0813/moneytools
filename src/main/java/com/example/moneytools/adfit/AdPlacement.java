package com.example.moneytools.adfit;

public enum AdPlacement {
    CALCULATOR_POST_TOOL("calculator_post_tool"),
    CALCULATOR_ARTICLE_MID("calculator_article_mid"),
    CALCULATOR_PRE_FAQ("calculator_pre_faq"),
    GUIDE_ARTICLE_MID("guide_article_mid"),
    GUIDE_PRE_FAQ("guide_pre_faq"),
    GUIDE_INDEX("guide_index"),
    HOME_MID("home_mid");

    private final String key;

    AdPlacement(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public static AdPlacement fromKey(String key) {
        for (AdPlacement placement : values()) {
            if (placement.key.equals(key)) {
                return placement;
            }
        }
        throw new IllegalArgumentException("Unknown AdFit placement: " + key);
    }
}
