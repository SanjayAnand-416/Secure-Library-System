package com.SecureLibrarySystem.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.dao.UserDAO;
import com.SecureLibrarySystem.webapp.hashing.PasswordHasher;
import com.SecureLibrarySystem.webapp.model.User;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Service
public class LoginService {

    @Autowired
    private UserDAO userDAO;


    @Autowired
    private EmailOTPService emailOTPService;
    
    @Autowired
    private HttpSession session;

    public boolean loginAndSendOtp(String username, String password) {

        // Since username is encrypted in database, we need to fetch all users
        // and find the matching one after decryption (JPA converter handles decryption)
        List<User> allUsers = userDAO.findAll();
        User user = null;
        
        for (User u : allUsers) {
            if (u.getUsername() != null && u.getUsername().equals(username)) {
                user = u;
                break;
            }
        }

        if (user == null) return false;

        if (PasswordHasher.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {

            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().name());
            session.setAttribute("otpEmail", user.getEmail());
            session.setAttribute("OTP_REQUIRED", true);

            emailOTPService.sendOTP(user.getEmail());
            return true;
        }

        return false;

    }

}
