package com.SecureLibrarySystem.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SecureLibrarySystem.webapp.dao.UserDAO;
import com.SecureLibrarySystem.webapp.authorization.Role;
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

    @GetMapping("/users/edit/{id}")
    public String editUserPage(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("ADMIN")) {
            return "redirect:/dashboard";
        }

        User user = userDAO.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/users";
        }

        model.addAttribute("user", user);
        model.addAttribute("role", role);
        return "edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String role,
            HttpSession session) {

        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String sessionRole = (String) session.getAttribute("role");
        if (sessionRole == null || !sessionRole.equals("ADMIN")) {
            return "redirect:/dashboard";
        }

        User user = userDAO.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/users";
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Role.valueOf(role.toUpperCase()));
        userDAO.save(user);

        return "redirect:/users";
    }
}
