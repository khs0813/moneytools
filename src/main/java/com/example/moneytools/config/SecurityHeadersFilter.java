package com.example.moneytools.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter extends OncePerRequestFilter {
    public static final String CSP_NONCE_ATTRIBUTE = "cspNonce";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Locale DEFAULT_SITE_LOCALE = Locale.KOREA;

    private static final String CONTENT_SECURITY_POLICY_REPORT_ONLY = String.join("; ",
            "default-src 'self'",
            "base-uri 'self'",
            "object-src 'none'",
            "frame-ancestors 'none'",
            "img-src 'self' data: https:",
            "font-src 'self' data:",
            "style-src 'self' 'unsafe-inline'",
            "script-src 'self' 'unsafe-inline' https:",
            "connect-src 'self' https:",
            "frame-src https:",
            "upgrade-insecure-requests");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String nonce = generateNonce();
        request.setAttribute(CSP_NONCE_ATTRIBUTE, nonce);
        response.setLocale(DEFAULT_SITE_LOCALE);
        response.setHeader("Content-Language", "ko-KR");
        response.setHeader("Content-Security-Policy-Report-Only", CONTENT_SECURITY_POLICY_REPORT_ONLY);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=(), usb=()");
        response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
        response.setHeader("Origin-Agent-Cluster", "?1");
        response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
        response.setHeader("X-Download-Options", "noopen");
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
        }
        filterChain.doFilter(request, response);
    }

    private String generateNonce() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
