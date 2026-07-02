package com.example.sessioncookie.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            @CookieValue(value = "lastVisit", required=false) String lastVisit,
            @CookieValue(value = "theme", defaultValue = "light") String theme,
            HttpServletResponse response,
            Model model
    ) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", username);
        model.addAttribute("theme", theme);

        if (lastVisit != null) {
            long ms = Long.parseLong(lastVisit);
            String date = Instant.ofEpochMilli(ms)
                    .atZone(ZoneId.systemDefault())
                    .format(FMT);
            model.addAttribute("lastVisit", date);
        }

        Cookie visit = new Cookie("lastVisit", String.valueOf(System.currentTimeMillis()));
        visit.setMaxAge(30 * 24 * 60 * 60);
        visit.setPath("/");
        visit.setHttpOnly(true);
        response.addCookie(visit);

        return "dashboard";
    }

    @GetMapping("/theme")
    public String setTheme(
            @RequestParam String mode,
            HttpServletResponse response
    ) {
        String value = "dark".equals(mode) ? "dark" : "light";
        Cookie theme = new Cookie("theme", value);
        theme.setMaxAge(30 * 24 * 60 * 60);
        theme.setPath("/");
        response.addCookie(theme);

        return "redirect:/dashboard";
    }
}
