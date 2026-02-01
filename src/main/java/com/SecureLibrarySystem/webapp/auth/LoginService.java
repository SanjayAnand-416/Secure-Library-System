package com.SecureLibrarySystem.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.dao.UserDAO;
import com.SecureLibrarySystem.webapp.hashing.PasswordHasher;
import com.SecureLibrarySystem.webapp.model.User;

import jakarta.servlet.http.HttpSession;

@Service
public class LoginService {

    @Autowired
    private UserDAO userDAO;


    @Autowired
    private EmailOTPService emailOTPService;
    
    @Autowired
    private HttpSession session;

    public boolean loginAndSendOtp(String username, String password) {

        User user = userDAO.findByUsername(username);

        if (user == null) return false;

        if (PasswordHasher.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {

            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().name());
            session.setAttribute("otpEmail", user.getEmail());

            emailOTPService.sendOTP(user.getEmail());
            return true;
        }

        return false;

    }

}
