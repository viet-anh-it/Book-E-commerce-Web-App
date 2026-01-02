package com.bookommerce.auth_server.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    public Map<String, Object> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Set<String> authorityNames = authorities.stream().map(authority -> authority.getAuthority()).collect(Collectors.toSet());
        return Map.of("username", username, "authorities", authorityNames);
    }
}
