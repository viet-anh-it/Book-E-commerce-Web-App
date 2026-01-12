package com.bookommerce.be_for_fe.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.be_for_fe.dto.ApiSuccessResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@RestController
@RequestMapping("/protected/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthCheckController {

    @GetMapping("/me")
    public ResponseEntity<ApiSuccessResponse<Map<String, Object>>> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Set<String> authorityNames = authorities.stream().map(authority -> authority.getAuthority()).collect(Collectors.toSet());
        Map<String, Object> data = Map.of("username", username, "authorities", authorityNames);
        return ResponseEntity.ok(ApiSuccessResponse.<Map<String, Object>>builder()
            .status(HttpStatus.OK.value())
            .message("Get authenticated user successfully")
            .data(data)
            .build());
    }
}
