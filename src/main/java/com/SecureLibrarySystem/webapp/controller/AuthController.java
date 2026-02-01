package com.SecureLibrarySystem.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.SecureLibrarySystem.webapp.auth.EmailOTPService;
import com.SecureLibrarySystem.webapp.auth.LoginService;
import com.SecureLibrarySystem.webapp.auth.RegisterService;
import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.dao.UserDAO;
import com.SecureLibrarySystem.webapp.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private EmailOTPService emailOTPService;

    @Autowired
    private UserDAO userDAO;

    /* ======================
       REGISTER
       ====================== */
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam String email) {

        Role userRole = Role.valueOf(role.toUpperCase());

        boolean success = registerService.registerUser(
                username, password, userRole, email
        );

        return success
                ? "redirect:/login-page?registered=true"
                : "redirect:/register-page?error=true";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        boolean valid = loginService.loginAndSendOtp(username, password);

        if (valid) {
            session.setAttribute("loginUser", username);
            session.setAttribute("OTP_REQUIRED", true);
            return "redirect:/login-page";
        }
        return "redirect:/login-page?error=true";
    }

    /* ======================
       VERIFY OTP
       ====================== */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam(required = false) String email,
            @RequestParam String otp,
            HttpSession session) {

        String sessionEmail = (String) session.getAttribute("otpEmail");
        String effectiveEmail = (email != null && !email.isBlank()) ? email : sessionEmail;

        if (effectiveEmail == null || effectiveEmail.isBlank()) {
            return "redirect:/login-page?otpError=true";
        }

        if (!emailOTPService.verifyOTP(effectiveEmail, otp)) {
            return "redirect:/login-page?otpError=true";
        }

        User user = userDAO.findByEmail(effectiveEmail);

        session.removeAttribute("OTP_REQUIRED");
        session.removeAttribute("otpEmail");
        session.setAttribute("AUTHENTICATED", true);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().name());

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login-page";
    }
}
