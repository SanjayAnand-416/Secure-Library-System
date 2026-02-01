package com.SecureLibrarySystem.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class PageController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login-page";
    }

    @GetMapping("/login-page")
    public String loginPage(HttpSession session, Model model) {
        Object otpRequired = session.getAttribute("OTP_REQUIRED");
        model.addAttribute("otpRequired", otpRequired != null && (Boolean) otpRequired);
        return "login";   // login.html
    }

    @GetMapping("/register-page")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/otp-page")
    public String otpPage() {
        return "otp";     // otp.html (NOTE: your file is otp.html, not opt.html)
    }


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        model.addAttribute("role", session.getAttribute("role"));
        return "dashboard";
    }






}
