package com.SecureLibrarySystem.webapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SecureLibrarySystem.webapp.model.User;

public interface UserDAO extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);
}
