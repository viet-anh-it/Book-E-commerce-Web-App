package com.bookommerce.auth_server.interceptor;

import java.util.HashSet;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @formatter:off
public class LoginSignupPageInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler) throws Exception {
        if(isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
            SimpleGrantedAuthority roleCustomer = new SimpleGrantedAuthority("ROLE_CUSTOMER");
            boolean hasRoleCustomer = authorities.contains(roleCustomer);
            if (hasRoleCustomer) {
                response.sendRedirect("http://app.bookommerce.com:8080");
                return false;
            } else {
                response.sendRedirect("http://admin.bookommerce.com:7979");
                return false;
            }
        }
        return true;
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getClass().isAssignableFrom(AnonymousAuthenticationToken.class)) {
            return false;
        }
        return true;
    }
}
