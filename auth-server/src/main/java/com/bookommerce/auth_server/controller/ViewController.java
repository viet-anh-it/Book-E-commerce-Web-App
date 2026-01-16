package com.bookommerce.auth_server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class ViewController {

    @GetMapping("/login/customer")
    public String renderCustomerLoginPage() {
        return "customer-login";
    }

    @GetMapping("/login/store")
    public String renderStoreLoginPage() {
        return "store-login";
    }

    @GetMapping("/signup")
    public String renderSignupPage() {
        return "signup";
    }

    @GetMapping("/signup/success")
    public String renderSignupSuccessPage() {
        return "signup-success";
    }

    @GetMapping("/account/activate/expire")
    public String renderActivationTokenExpiredPage() {
        return "activation-token-expired";
    }

    @GetMapping("/account/activate/error")
    public String renderActivationTokenErrorPage() {
        return "activation-token-error";
    }
}
