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

        // Email is encrypted in DB, so find user by iterating after decryption
        List<User> allUsers = userDAO.findAll();
        User user = null;
        for (User u : allUsers) {
            if (u.getEmail() != null && u.getEmail().equals(effectiveEmail)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            return "redirect:/login-page?otpError=true";
        }

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
