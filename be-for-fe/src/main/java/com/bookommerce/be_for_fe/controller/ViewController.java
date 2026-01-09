package com.bookommerce.be_for_fe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class ViewController {

    @GetMapping("/confirm-logout")
    public String renderLogoutConfirmationPage() {
        return "logout-confirmation";
    }
}
