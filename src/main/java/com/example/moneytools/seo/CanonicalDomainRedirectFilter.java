package com.example.moneytools.seo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CanonicalDomainRedirectFilter extends OncePerRequestFilter {
    private static final String APEX_HOST = "moneycalculator.co.kr";
    private static final String CANONICAL_HOST = "www.moneycalculator.co.kr";
    private static final String CANONICAL_ORIGIN = "https://" + CANONICAL_HOST;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String method = request.getMethod();
        if (("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) && shouldRedirect(request)) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", canonicalLocation(request));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldRedirect(HttpServletRequest request) {
        String host = normalizedHost(request);
        if (!(APEX_HOST.equals(host) || CANONICAL_HOST.equals(host))) {
            return false;
        }
        return APEX_HOST.equals(host) || !"https".equalsIgnoreCase(forwardedProto(request));
    }

    private String canonicalLocation(HttpServletRequest request) {
        StringBuilder location = new StringBuilder(CANONICAL_ORIGIN).append(request.getRequestURI());
        String query = request.getQueryString();
        if (query != null && !query.isBlank()) {
            location.append('?').append(query);
        }
        return location.toString();
    }

    private String normalizedHost(HttpServletRequest request) {
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = request.getHeader("Host");
        }
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }
        String firstHost = host.split(",", 2)[0].trim().toLowerCase();
        int portSeparator = firstHost.indexOf(':');
        return portSeparator >= 0 ? firstHost.substring(0, portSeparator) : firstHost;
    }

    private String forwardedProto(HttpServletRequest request) {
        String proto = request.getHeader("X-Forwarded-Proto");
        if (proto == null || proto.isBlank()) {
            return request.getScheme();
        }
        return proto.split(",", 2)[0].trim();
    }
}
