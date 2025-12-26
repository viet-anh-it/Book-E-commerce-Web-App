package com.bookommerce.auth_server.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.auth_server.dto.request.LoginRequestDto;
import com.bookommerce.auth_server.dto.request.RegistrationRequestDto;
import com.bookommerce.auth_server.dto.response.ApiSuccessResponse;
import com.bookommerce.auth_server.service.AuthService;
import com.bookommerce.auth_server.validation.ValidationOrder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiSuccessResponse<Void>> register(
            @RequestBody @Validated(ValidationOrder.class) RegistrationRequestDto registrationRequestDto) {
        this.authService.register(registrationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .build());
    }

    //@formatter:off
    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<Map<String, String>>> login(
        @RequestBody @Valid LoginRequestDto loginRequestDto,
        HttpServletRequest request,
        HttpServletResponse response) {
        Map<String, String> responseData = this.authService.login(loginRequestDto, request, response);
        return ResponseEntity.status(HttpStatus.OK).body(ApiSuccessResponse.<Map<String, String>>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(responseData)
                .build());
    }
}