package com.example.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    @GetMapping("/join")
    public String signUp() {
        return "sign-up";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
