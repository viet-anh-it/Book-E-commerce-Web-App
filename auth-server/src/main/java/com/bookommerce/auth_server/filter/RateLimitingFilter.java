package com.bookommerce.auth_server.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RateLimitingFilter extends OncePerRequestFilter {

    static int THRESHOLD = 1;
    static long WINDOW_SIZE_MS = 5_000;
    Map<String, Window> ipStorage = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String clientIp = getClientIp(request);

        if (isAllowed(clientIp)) {
            filterChain.doFilter(request, response);
        } else {
            handleRateLimitExceeded(response);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/page");
    }

    private boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();
        Window window = ipStorage.computeIfAbsent(ip, k -> new Window(now));

        if (now - window.startTime > WINDOW_SIZE_MS) {
            synchronized (window) {
                if (now - window.startTime > WINDOW_SIZE_MS) {
                    window.startTime = now;
                    window.counter.set(1);
                    return true;
                }
            }
        }

        return window.counter.incrementAndGet() <= THRESHOLD;
    }

    private void handleRateLimitExceeded(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String jsonResponse = "{\"status\": 429, \"message\": \"You have sent too many requests. Please try again after " + WINDOW_SIZE_MS / 1000 + " seconds.\"}";
        response.getWriter().write(jsonResponse);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private static class Window {
        final AtomicInteger counter = new AtomicInteger(0);
        volatile long startTime;

        Window(long startTime) {
            this.startTime = startTime;
        }
    }
}
