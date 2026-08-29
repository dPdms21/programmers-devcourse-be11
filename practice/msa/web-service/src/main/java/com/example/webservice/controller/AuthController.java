package com.example.webservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class AuthController {
    @GetMapping("/join")
    public String join() {
        return "/auth/sign-up";
    }

    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }
}
