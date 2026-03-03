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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

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

        RegisterService.RegisterResult result = registerService.registerUser(
                username, password, userRole, email
        );

        if (result == RegisterService.RegisterResult.SUCCESS) {
            return "redirect:/login-page?registered=true";
        } else if (result == RegisterService.RegisterResult.USERNAME_EXISTS) {
            return "redirect:/register-page?usernameExists=true";
        } else if (result == RegisterService.RegisterResult.EMAIL_EXISTS) {
            return "redirect:/register-page?emailExists=true";
        } else {
            return "redirect:/register-page?error=true";
        }
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        boolean valid = loginService.loginAndSendOtp(username, password);

        if (valid) {
            // Credentials verified; OTP state already set in session by LoginService
            // loginService sets: OTP_REQUIRED=true, otpEmail, username, role
            return "redirect:/otp-page?sent=true";
        }
        // Invalid credentials
        return "redirect:/login-page?error=true";
    }

    /* ======================
       VERIFY OTP
       ====================== */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session) {

        Boolean otpRequired = (Boolean) session.getAttribute("OTP_REQUIRED");
        if (otpRequired == null || !otpRequired) {
            return "redirect:/login-page";
        }

        // Always use the session email — it is the exact key used to store the OTP
        String sessionEmail = (String) session.getAttribute("otpEmail");

        if (sessionEmail == null || sessionEmail.isBlank()) {
            return "redirect:/login-page";
        }

        if (!emailOTPService.verifyOTP(sessionEmail, otp)) {
            return "redirect:/otp-page?error=true";
        }

        // Email is encrypted in DB, so find user by iterating after decryption
        List<User> allUsers = userDAO.findAll();
        User user = null;
        for (User u : allUsers) {
            if (u.getEmail() != null && u.getEmail().equals(sessionEmail)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            return "redirect:/otp-page?error=true";
        }

        session.removeAttribute("OTP_REQUIRED");
        session.removeAttribute("otpEmail");
        session.setAttribute("AUTHENTICATED", true);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().name());

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/login-page";
    }
}
