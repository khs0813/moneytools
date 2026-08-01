package com.example.moneytools.config;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webManifestMimeMapping() {
        return factory -> {
            MimeMappings mappings = new MimeMappings();
            mappings.add("webmanifest", "application/manifest+json");
            factory.addMimeMappings(mappings);
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/service-worker.js", "/site.webmanifest")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache());
        registry.addResourceHandler("/css/**", "/js/**", "/img/**", "/icons/**", "/favicon.svg", "/offline.html")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/", "classpath:/static/img/", "classpath:/static/icons/", "classpath:/static/", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());
    }
}
