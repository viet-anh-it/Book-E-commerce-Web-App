package com.bookommerce.auth_server.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.auth_server.dto.response.ApiSuccessResponse;
import com.bookommerce.auth_server.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiSuccessResponse<Map<String, Object>>> getAuthenticatedUser() {
        return ResponseEntity.ok(ApiSuccessResponse.<Map<String, Object>>builder()
                .status(HttpStatus.OK.value())
                .message("Get authenticated user successfully")
                .data(this.userService.getAuthenticatedUser())
                .build());
    }
}
