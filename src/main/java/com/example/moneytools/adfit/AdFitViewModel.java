package com.example.moneytools.adfit;

public record AdFitViewModel(
        boolean v2Enabled,
        boolean pcSideEnabled,
        boolean secondaryEnabled,
        String resultMobileUnit,
        String resultPcUnit,
        String pcSideUnit,
        String secondaryMobileUnit,
        String secondaryPcUnit
) {
    public boolean hasResultMobileUnit() {
        return !resultMobileUnit.isBlank();
    }

    public boolean hasResultPcUnit() {
        return !resultPcUnit.isBlank();
    }

    public boolean hasPcSideUnit() {
        return pcSideEnabled && !pcSideUnit.isBlank();
    }

    public boolean hasSecondaryMobileUnit() {
        return secondaryEnabled && !secondaryMobileUnit.isBlank();
    }

    public boolean hasSecondaryPcUnit() {
        return secondaryEnabled && !secondaryPcUnit.isBlank();
    }
}
