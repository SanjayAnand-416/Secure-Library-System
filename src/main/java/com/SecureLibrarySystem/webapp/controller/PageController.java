package com.SecureLibrarySystem.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class PageController {

    @GetMapping("/")
    public String root(HttpSession session) {
        // If already authenticated, go straight to dashboard
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth != null && auth) {
            return "redirect:/dashboard";
        }
        return "redirect:/login-page";
    }

    @GetMapping("/login-page")
    public String loginPage(HttpServletRequest request, HttpSession session, Model model) {
        // If user is already authenticated, force fresh login (back-button protection)
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth != null && auth) {
            session.invalidate();
            session = request.getSession(true);
        }

        // Check if we're in OTP flow (user entered correct credentials)
        Object otpRequired = session.getAttribute("OTP_REQUIRED");
        boolean inOtpFlow = otpRequired != null && (Boolean) otpRequired;
        
        // Only clear OTP state if NOT in active OTP flow
        if (!inOtpFlow) {
            session.removeAttribute("otpEmail");
            session.removeAttribute("loginUser");
        }

        model.addAttribute("otpRequired", inOtpFlow);
        return "login";   // login.html
    }

    @GetMapping("/register-page")
    public String registerPage(HttpSession session) {
        // If already authenticated, redirect to dashboard
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth != null && auth) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @GetMapping("/otp-page")
    public String otpPage(HttpServletRequest request, HttpSession session) {
        // If already authenticated, redirect to dashboard (prevent post-auth back button access)
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth != null && auth) {
            return "redirect:/dashboard";
        }

        // OTP page is ONLY valid during an active OTP challenge (after login returned true)
        Boolean otpRequired = (Boolean) session.getAttribute("OTP_REQUIRED");
        if (otpRequired == null || !otpRequired) {
            return "redirect:/login-page";
        }

        return "otp";     // otp.html
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
