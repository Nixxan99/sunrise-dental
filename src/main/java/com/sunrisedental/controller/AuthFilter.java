package com.sunrisedental.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Authentication and Session Security Filter.
 * Protects secured views, appointment management, and billing operations
 * ensuring only authenticated staff members can access them.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    // Publicly accessible URI substrings/endpoints that bypass auth checks
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login",
            "/logout",
            "/views/login.jsp",
            "/css/",
            "/js/",
            "/images/",
            "/static/",
            "/assets/",
            "/api/",
            "/favicon.ico"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // Check if current path is a public resource
        boolean isPublic = isPublicPath(path);

        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (isPublic || isLoggedIn) {
            // Add cache control headers for secured paths to prevent browsing history caching
            if (isLoggedIn && !isStaticResource(path)) {
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
            }
            chain.doFilter(req, res);
        } else {
            // Protected path and user is not authenticated -> redirect to login
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
        }
    }

    private boolean isPublicPath(String path) {
        if (path == null || path.isEmpty() || "/".equals(path)) {
            return true;
        }

        for (String publicPrefix : PUBLIC_PATHS) {
            if (path.startsWith(publicPrefix) || path.equals(publicPrefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/images/") || path.startsWith("/assets/")
                || path.endsWith(".css") || path.endsWith(".js")
                || path.endsWith(".png") || path.endsWith(".jpg")
                || path.endsWith(".ico");
    }

    @Override
    public void destroy() {
        // Cleanup resources if needed
    }
}
