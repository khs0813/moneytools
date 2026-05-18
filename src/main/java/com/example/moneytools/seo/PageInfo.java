package com.example.moneytools.seo;

import java.time.LocalDate;

public record PageInfo(
        String key,
        String path,
        String label,
        String title,
        String description,
        boolean inNavigation,
        boolean inSitemap,
        LocalDate lastModified
) {
}
