package com.example.moneytools.seo;

import com.example.moneytools.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SeoServiceTests {
    @Test
    void escapesHtmlSensitiveCharactersInJsonLd() {
        AppProperties appProperties = new AppProperties();
        appProperties.setName("</script><script>alert(1)</script>");
        PublicUrlService publicUrlService = new PublicUrlService(appProperties);
        SeoService seoService = new SeoService(appProperties, new ObjectMapper(), publicUrlService);
        PageInfo page = new PageInfo("test", "/test", "테스트", "테스트", "설명", true, true, LocalDate.of(2026, 1, 1));

        String json = seoService.structuredData(page, List.of());

        assertThat(json).doesNotContain("</script>");
        assertThat(json).contains("\\u003C/script\\u003E");
    }
}
