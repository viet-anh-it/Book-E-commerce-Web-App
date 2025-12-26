package com.bookommerce.auth_server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class ViewController {

    @GetMapping("/login")
    public String renderLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String renderSignupPage() {
        return "signup";
    }
}
