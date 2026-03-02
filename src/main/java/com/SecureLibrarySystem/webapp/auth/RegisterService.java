package com.SecureLibrarySystem.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.dao.UserDAO;
import com.SecureLibrarySystem.webapp.hashing.PasswordHasher;
import com.SecureLibrarySystem.webapp.model.User;
import java.util.List;

@Service
public class RegisterService {

    @Autowired
    private UserDAO userDAO;

    public enum RegisterResult {
        SUCCESS,
        USERNAME_EXISTS,
        EMAIL_EXISTS
    }

    public RegisterResult registerUser(String username, String password, Role role, String email) {

        // Since username/email are encrypted, fetch all users and check after decryption
        List<User> allUsers = userDAO.findAll();
        
        for (User u : allUsers) {
            if (u.getUsername() != null && u.getUsername().equals(username)) {
                return RegisterResult.USERNAME_EXISTS;
            }
            if (u.getEmail() != null && u.getEmail().equals(email)) {
                return RegisterResult.EMAIL_EXISTS;
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);

        PasswordHasher.HashResult hash = PasswordHasher.hash(password);
        user.setPasswordHash(hash.getHash());
        user.setSalt(hash.getSalt());

        userDAO.save(user);
        return RegisterResult.SUCCESS;
    }

}
