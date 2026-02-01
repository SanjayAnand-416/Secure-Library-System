package com.SecureLibrarySystem.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.SecureLibrarySystem.webapp.dao.UserDAO;
import com.SecureLibrarySystem.webapp.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserManagementController {

    @Autowired
    private UserDAO userDAO;

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (role == null || !(role.equals("LIBRARIAN") || role.equals("ADMIN"))) {
            return "redirect:/dashboard";
        }

        List<User> users = userDAO.findAll();
        model.addAttribute("users", users);
        model.addAttribute("role", role);
        return "users";
    }
}
