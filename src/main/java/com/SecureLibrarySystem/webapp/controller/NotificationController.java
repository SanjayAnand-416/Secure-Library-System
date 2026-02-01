package com.SecureLibrarySystem.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.service.BookRequestService;
import com.SecureLibrarySystem.webapp.service.NotificationService;

import jakarta.servlet.http.HttpSession;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BookRequestService bookRequestService;

    @GetMapping("/notifications")
    public String viewNotifications(HttpSession session, Model model) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!("ADMIN".equals(role) || "LIBRARIAN".equals(role) || "STUDENT".equals(role))) {
            return "redirect:/dashboard";
        }

        if ("ADMIN".equals(role) || "LIBRARIAN".equals(role)) {
            bookRequestService.generateOverdueNotifications();
            model.addAttribute("notifications", notificationService.getNotificationsFor(Role.valueOf(role)));
        } else {
            String username = (String) session.getAttribute("username");
            model.addAttribute("notifications", notificationService.getNotificationsForUser(username));
        }
        model.addAttribute("role", role);
        return "notifications";
    }
}
