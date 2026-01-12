package com.bookommerce.be_for_fe.interceptor;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @formatter:off
public class SessionCookieMaxAgeSlidingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String sessionId = session.getId();
        Cookie cookie = new Cookie("BFF-SESSION", sessionId);
        cookie.setPath("/protected");
        cookie.setDomain("bff.bookommerce.com");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(60 * 3);
        response.addCookie(cookie);
        return true;
    }
}
