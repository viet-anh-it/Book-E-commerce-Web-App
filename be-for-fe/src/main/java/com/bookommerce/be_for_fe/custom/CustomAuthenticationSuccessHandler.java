package com.bookommerce.be_for_fe.custom;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @formatter:off
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        SimpleGrantedAuthority roleCustomer = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        if (authorities.contains(roleCustomer)) {
            response.sendRedirect("https://app.bookommerce.com:8080");
        } else {
            response.sendRedirect("https://admin.bookommerce.com:7979");
        }
    }
    
}
