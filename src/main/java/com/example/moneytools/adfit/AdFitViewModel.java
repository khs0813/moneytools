package com.example.moneytools.adfit;

import java.util.EnumSet;
import java.util.Set;

public class AdFitViewModel {
    private final AdFitProperties properties;
    private final String path;
    private final String host;
    private final PageKind pageKind;

    public AdFitViewModel(AdFitProperties properties, String path, String host, PageKind pageKind) {
        this.properties = properties;
        this.path = path;
        this.host = host;
        this.pageKind = pageKind;
    }

    public boolean enabledForPage() {
        return properties.isEnabled()
                && properties.isAllowedHost(host)
                && pageKind != PageKind.BLOCKED
                && !allowedPlacements().isEmpty();
    }

    public boolean hasRenderablePageSlot() {
        return enabledForPage() && allowedPlacements().stream().anyMatch(placement -> slot(placement.key()).renderable());
    }

    public AdFitSlotViewModel slot(String placementKey) {
        AdPlacement placement = AdPlacement.fromKey(placementKey);
        if (!enabledForPage() || !allowedPlacements().contains(placement)) {
            return AdFitSlotViewModel.empty(placement.key());
        }
        if (placement == AdPlacement.CALCULATOR_PRE_FAQ && !properties.isCalcPreFaqAllowed(path)) {
            return AdFitSlotViewModel.empty(placement.key());
        }
        return properties.slotFor(placement);
    }

    public String pageKind() {
        return pageKind.name().toLowerCase();
    }

    private Set<AdPlacement> allowedPlacements() {
        return switch (pageKind) {
            case HOME -> EnumSet.of(AdPlacement.HOME_MID);
            case CALCULATOR -> EnumSet.of(
                    AdPlacement.CALCULATOR_POST_TOOL,
                    AdPlacement.CALCULATOR_ARTICLE_MID,
                    AdPlacement.CALCULATOR_PRE_FAQ);
            case GUIDE_INDEX -> EnumSet.of(AdPlacement.GUIDE_INDEX);
            case GUIDE_ARTICLE_LONG -> EnumSet.of(AdPlacement.GUIDE_ARTICLE_MID, AdPlacement.GUIDE_PRE_FAQ);
            case GUIDE_ARTICLE_SHORT -> EnumSet.of(AdPlacement.GUIDE_ARTICLE_MID);
            case BLOCKED -> EnumSet.noneOf(AdPlacement.class);
        };
    }

    public enum PageKind {
        HOME,
        CALCULATOR,
        GUIDE_INDEX,
        GUIDE_ARTICLE_LONG,
        GUIDE_ARTICLE_SHORT,
        BLOCKED
    }
}
