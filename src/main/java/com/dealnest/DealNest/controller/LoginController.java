package com.dealnest.DealNest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/user/authenticate")
    public String authenticateUser(@RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   Model model) {
        // Simple validation
        if (email.isEmpty() || password.isEmpty()) {
            model.addAttribute("emptyerror", "Username and password cannot be empty");
            return "login"; // Return to login page with error
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            // If authentication succeeds, redirect to success page
            return "redirect:/dealnest/user/";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login"; // Return to login page with error
        }
    }
}

