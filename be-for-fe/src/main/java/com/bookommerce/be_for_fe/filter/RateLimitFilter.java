package com.bookommerce.be_for_fe.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RateLimitFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    static int THRESHOLD = 20;
    static long WINDOW_SIZE_MS = 60_000;
    Map<String, Window> ipStorage = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public ServerResponse filter(
        @NonNull ServerRequest request,
        @NonNull HandlerFunction<ServerResponse> next
    ) throws Exception {
        String ip = request.remoteAddress()
            .map(addr -> addr.getAddress().getHostAddress())
            .orElse("unknown");

        if (isAllowed(ip)) {
            return next.handle(request);
        } else {
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate limit exceeded. Please try again in a minute.");
        }
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

    private static class Window {
        final AtomicInteger counter = new AtomicInteger(0);
        volatile long startTime;

        Window(long startTime) {
            this.startTime = startTime;
        }
    }
}