package com.example.moneytools.seo;

import com.example.moneytools.config.AppProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeoService {
    private static final ZoneId SEO_TIME_ZONE = ZoneId.of("Asia/Seoul");

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final PublicUrlService publicUrlService;

    public SeoService(AppProperties appProperties, ObjectMapper objectMapper, PublicUrlService publicUrlService) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
        this.publicUrlService = publicUrlService;
    }

    public String structuredData(PageInfo page, List<FaqItem> faqItems) {
        List<Map<String, Object>> graph = new ArrayList<>();
        graph.add(organization());
        graph.add(website());
        graph.add(webPage(page));
        if (page.calculator()) {
            graph.add(webApplication(page));
        }
        if (page.key().startsWith("guide-")) {
            graph.add(article(page));
        }
        graph.add(breadcrumb(page));
        if (faqItems != null && !faqItems.isEmpty()) {
            graph.add(faqPage(page, faqItems));
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("@context", "https://schema.org");
        root.put("@graph", graph);
        return toJson(root);
    }

    private Map<String, Object> organization() {
        Map<String, Object> organization = new LinkedHashMap<>();
        organization.put("@type", "Organization");
        organization.put("@id", publicUrlService.absoluteUrl("/") + "#organization");
        organization.put("name", appProperties.getName());
        organization.put("url", publicUrlService.absoluteUrl("/"));
        organization.put("email", appProperties.getContactEmail());
        organization.put("contactPoint", Map.of(
                "@type", "ContactPoint",
                "email", appProperties.getContactEmail(),
                "contactType", "customer support"
        ));
        organization.put("logo", publicUrlService.absoluteUrl("/og-image.png"));
        return organization;
    }

    private Map<String, Object> website() {
        Map<String, Object> site = new LinkedHashMap<>();
        site.put("@type", "WebSite");
        site.put("@id", publicUrlService.absoluteUrl("/") + "#website");
        site.put("name", appProperties.getName());
        site.put("url", publicUrlService.absoluteUrl("/"));
        site.put("description", appProperties.getDescription());
        site.put("publisher", Map.of("@id", publicUrlService.absoluteUrl("/") + "#organization"));
        site.put("inLanguage", "ko-KR");
        return site;
    }

    private Map<String, Object> webPage(PageInfo page) {
        String pageUrl = publicUrlService.absoluteUrl(page.path());
        Map<String, Object> webPage = new LinkedHashMap<>();
        webPage.put("@type", "WebPage");
        webPage.put("@id", pageUrl + "#webpage");
        webPage.put("url", pageUrl);
        webPage.put("name", page.title());
        webPage.put("description", page.description());
        webPage.put("isPartOf", Map.of("@id", publicUrlService.absoluteUrl("/") + "#website"));
        webPage.put("about", Map.of("@id", publicUrlService.absoluteUrl("/") + "#organization"));
        webPage.put("inLanguage", "ko-KR");
        webPage.put("image", publicUrlService.absoluteUrl("/og-image.png"));
        webPage.put("primaryImageOfPage", publicUrlService.absoluteUrl("/og-image.png"));
        webPage.put("dateModified", modifiedDateTime(page));
        return webPage;
    }

    private Map<String, Object> webApplication(PageInfo page) {
        String pageUrl = publicUrlService.absoluteUrl(page.path());
        Map<String, Object> app = new LinkedHashMap<>();
        app.put("@type", "WebApplication");
        app.put("@id", pageUrl + "#webapplication");
        app.put("name", page.label());
        app.put("url", pageUrl);
        app.put("applicationCategory", "FinanceApplication");
        app.put("operatingSystem", "Web");
        app.put("description", page.description());
        app.put("inLanguage", "ko-KR");
        app.put("isAccessibleForFree", true);
        app.put("offers", Map.of(
                "@type", "Offer",
                "price", "0",
                "priceCurrency", "KRW"
        ));
        return app;
    }

    private Map<String, Object> article(PageInfo page) {
        String pageUrl = publicUrlService.absoluteUrl(page.path());
        Map<String, Object> article = new LinkedHashMap<>();
        article.put("@type", "Article");
        article.put("@id", pageUrl + "#article");
        article.put("mainEntityOfPage", Map.of("@id", pageUrl + "#webpage"));
        article.put("headline", page.title());
        article.put("description", page.description());
        article.put("url", pageUrl);
        article.put("image", publicUrlService.absoluteUrl("/og-image.png"));
        article.put("author", Map.of("@id", publicUrlService.absoluteUrl("/") + "#organization"));
        article.put("publisher", Map.of("@id", publicUrlService.absoluteUrl("/") + "#organization"));
        article.put("inLanguage", "ko-KR");
        article.put("datePublished", modifiedDateTime(page));
        article.put("dateModified", modifiedDateTime(page));
        return article;
    }

    private Map<String, Object> breadcrumb(PageInfo page) {
        Map<String, Object> breadcrumb = new LinkedHashMap<>();
        breadcrumb.put("@type", "BreadcrumbList");
        List<Map<String, Object>> items = new ArrayList<>();
        items.add(Map.of("@type", "ListItem", "position", 1, "name", "홈", "item", publicUrlService.absoluteUrl("/")));
        if (!"/".equals(page.path())) {
            items.add(Map.of("@type", "ListItem", "position", 2, "name", page.label(), "item", publicUrlService.absoluteUrl(page.path())));
        }
        breadcrumb.put("itemListElement", items);
        return breadcrumb;
    }

    private Map<String, Object> faqPage(PageInfo page, List<FaqItem> faqItems) {
        Map<String, Object> faqPage = new LinkedHashMap<>();
        faqPage.put("@type", "FAQPage");
        faqPage.put("@id", publicUrlService.absoluteUrl(page.path()) + "#faq");
        faqPage.put("mainEntity", faqItems.stream().map(item -> {
            Map<String, Object> question = new LinkedHashMap<>();
            question.put("@type", "Question");
            question.put("name", item.question());
            question.put("acceptedAnswer", Map.of("@type", "Answer", "text", item.answer()));
            return question;
        }).toList());
        return faqPage;
    }

    private String modifiedDateTime(PageInfo page) {
        return ZonedDateTime.of(page.lastModified(), LocalTime.NOON, SEO_TIME_ZONE).toOffsetDateTime().toString();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
                    .replace("<", "\\u003C")
                    .replace(">", "\\u003E")
                    .replace("&", "\\u0026");
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize structured data", e);
        }
    }
}
