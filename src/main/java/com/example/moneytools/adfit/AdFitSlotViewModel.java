package com.example.moneytools.adfit;

public record AdFitSlotViewModel(
        String placement,
        String desktopUnit,
        int desktopWidth,
        int desktopHeight,
        String mobileUnit,
        int mobileWidth,
        int mobileHeight
) {
    public static AdFitSlotViewModel empty(String placement) {
        return new AdFitSlotViewModel(placement, "", 0, 0, "", 0, 0);
    }

    public boolean renderable() {
        return !desktopUnit.isBlank() || !mobileUnit.isBlank();
    }
}
