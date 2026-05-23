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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter extends OncePerRequestFilter {
    private static final String CONTENT_SECURITY_POLICY = String.join("; ",
            "default-src 'self'",
            "base-uri 'self'",
            "object-src 'none'",
            "frame-ancestors 'self'",
            "form-action 'self'",
            "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://pagead2.googlesyndication.com https://googleads.g.doubleclick.net https://tpc.googlesyndication.com",
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net",
            "img-src 'self' data: https:",
            "font-src 'self' data:",
            "connect-src 'self'",
            "frame-src https://googleads.g.doubleclick.net https://tpc.googlesyndication.com",
            "upgrade-insecure-requests");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()");
        response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
        }
        filterChain.doFilter(request, response);
    }
}
