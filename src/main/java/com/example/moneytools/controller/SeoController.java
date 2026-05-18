package com.example.moneytools.controller;

import com.example.moneytools.config.AppProperties;
import com.example.moneytools.seo.PageInfo;
import com.example.moneytools.seo.PublicUrlService;
import com.example.moneytools.seo.SitePages;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@RestController
public class SeoController {
    private static final ZoneId SEO_TIME_ZONE = ZoneId.of("Asia/Seoul");

    private final AppProperties appProperties;
    private final PublicUrlService publicUrlService;

    public SeoController(AppProperties appProperties, PublicUrlService publicUrlService) {
        this.appProperties = appProperties;
        this.publicUrlService = publicUrlService;
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return "User-agent: *\n"
                + "Allow: /\n\n"
                + "Disallow: /admin/\n"
                + "Disallow: /internal/\n"
                + "Disallow: /test/\n\n"
                + "Sitemap: " + publicUrlService.absoluteUrl("/sitemap.xml") + "\n";
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (PageInfo page : SitePages.sitemap()) {
            xml.append("  <url>\n")
                    .append("    <loc>").append(escapeXml(publicUrlService.absoluteUrl(page.path()))).append("</loc>\n")
                    .append("    <lastmod>").append(page.lastModified()).append("</lastmod>\n")
                    .append("    <changefreq>").append("/".equals(page.path()) ? "daily" : "weekly").append("</changefreq>\n")
                    .append("    <priority>").append("/".equals(page.path()) ? "1.0" : "0.8").append("</priority>\n")
                    .append("  </url>\n");
        }
        xml.append("</urlset>");
        return xml.toString();
    }

    @GetMapping(value = "/rss.xml", produces = "application/rss+xml; charset=UTF-8")
    public String rss() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("<channel>\n");
        xml.append("  <title>").append(escapeXml(appProperties.getName())).append("</title>\n");
        xml.append("  <link>").append(escapeXml(publicUrlService.absoluteUrl("/"))).append("</link>\n");
        xml.append("  <description>").append(escapeXml(appProperties.getDescription())).append("</description>\n");
        xml.append("  <atom:link href=\"").append(escapeXml(publicUrlService.absoluteUrl("/rss.xml"))).append("\" rel=\"self\" type=\"application/rss+xml\" />\n");
        xml.append("  <language>ko-KR</language>\n");
        PageInfo latestPage = SitePages.sitemap().stream()
                .max(Comparator.comparing(PageInfo::lastModified))
                .orElse(SitePages.require("home"));
        xml.append("  <lastBuildDate>")
                .append(rfc1123(latestPage.lastModified().atTime(LocalTime.NOON).atZone(SEO_TIME_ZONE)))
                .append("</lastBuildDate>\n");
        for (PageInfo page : SitePages.sitemap()) {
            String itemUrl = publicUrlService.absoluteUrl(page.path());
            xml.append("  <item>\n")
                    .append("    <title>").append(escapeXml(page.title())).append("</title>\n")
                    .append("    <link>").append(escapeXml(itemUrl)).append("</link>\n")
                    .append("    <guid isPermaLink=\"true\">").append(escapeXml(itemUrl)).append("</guid>\n")
                    .append("    <description>").append(escapeXml(page.description())).append("</description>\n")
                    .append("    <pubDate>")
                    .append(rfc1123(page.lastModified().atTime(LocalTime.NOON).atZone(SEO_TIME_ZONE)))
                    .append("</pubDate>\n")
                    .append("    <content:encoded><![CDATA[")
                    .append("<article><h1>").append(escapeHtmlBody(page.label())).append("</h1><p>")
                    .append(escapeHtmlBody(page.description())).append("</p></article>")
                    .append("]]></content:encoded>\n")
                    .append("  </item>\n");
        }
        xml.append("</channel>\n");
        xml.append("</rss>");
        return xml.toString();
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String escapeHtmlBody(String value) {
        return escapeXml(value).replace("\n", " ");
    }

    private String rfc1123(ZonedDateTime dateTime) {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime);
    }
}
