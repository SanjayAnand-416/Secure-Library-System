package com.SecureLibrarySystem.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.dao.UserDAO;
import com.SecureLibrarySystem.webapp.hashing.PasswordHasher;
import com.SecureLibrarySystem.webapp.model.User;

@Service
public class RegisterService {

    @Autowired
    private UserDAO userDAO;

    public boolean registerUser(String username, String password, Role role, String email) {

        if (userDAO.findByUsername(username) != null) {
            return false; // user already exists
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);

        PasswordHasher.HashResult hash = PasswordHasher.hash(password);
        user.setPasswordHash(hash.getHash());
        user.setSalt(hash.getSalt());

        userDAO.save(user);
        return true;
    }

}
