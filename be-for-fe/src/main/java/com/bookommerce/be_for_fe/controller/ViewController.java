package com.bookommerce.be_for_fe.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/confirm-logout")
    public String renderLogoutConfirmationPage(Model model, CsrfToken csrfToken) {
        model.addAttribute("_csrf", csrfToken);
        return "logout-confirmation";
    }
}
