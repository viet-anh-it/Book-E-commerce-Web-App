package com.bookommerce.auth_server.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.auth_server.dto.request.LoginRequestDto;
import com.bookommerce.auth_server.dto.request.RegistrationRequestDto;
import com.bookommerce.auth_server.dto.response.ApiSuccessResponse;
import com.bookommerce.auth_server.exception.AccountActivationTokenExpiredException;
import com.bookommerce.auth_server.exception.AccountActivationTokenNotFoundException;
import com.bookommerce.auth_server.service.AuthService;
import com.bookommerce.auth_server.validation.ValidationOrder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    static String CUSTOMER_UI_BASE_URL = "https://app.bookommerce.com:8080";
    static String ACCOUNT_ACTIVATION_TOKEN_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
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


    @PostMapping(path = {"/login/customer", "/login/store"})
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

    @GetMapping("/account/activate")
    public void activateAccount(
        @RequestParam(required = false) String token,
        HttpServletResponse response) throws IOException {
        if (token == null) {
            response.sendRedirect("https://auth.bookommerce.com:8282/page/account/activate/error?account_activation_token_not_found");
            return;
        }

        if (!token.matches(ACCOUNT_ACTIVATION_TOKEN_PATTERN)) {
            response.sendRedirect("https://auth.bookommerce.com:8282/page/account/activate/error?invalid_account_activation_token_format");
            return;
        }

        try {
            this.authService.activateAccount(token);
            response.sendRedirect("https://auth.bookommerce.com:8282/page/login/customer?activation_success");
        } catch (AccountActivationTokenNotFoundException exception) {
            response.sendRedirect("https://auth.bookommerce.com:8282/page/account/activate/error?account_activation_token_not_found");
        } catch (AccountActivationTokenExpiredException exception) {
            response.sendRedirect("https://auth.bookommerce.com:8282/page/account/activate/expire");
        }
    }
}