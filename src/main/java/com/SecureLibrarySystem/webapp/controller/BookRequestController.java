package com.SecureLibrarySystem.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.SecureLibrarySystem.webapp.service.BookRequestService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/requests")
public class BookRequestController {

    @Autowired
    private BookRequestService bookRequestService;

    @GetMapping
    public String viewRequests(HttpSession session, Model model) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if ("ADMIN".equals(role) || "LIBRARIAN".equals(role)) {
            bookRequestService.generateOverdueNotifications();
            java.util.List<com.SecureLibrarySystem.webapp.model.BookRequest> requests = bookRequestService.getAllRequests();
            model.addAttribute("requests", requests);

            java.util.Set<String> usernames = new java.util.HashSet<>();
            for (com.SecureLibrarySystem.webapp.model.BookRequest request : requests) {
                if (request.getUsername() != null) {
                    usernames.add(request.getUsername());
                }
            }

            java.util.Map<String, java.util.List<com.SecureLibrarySystem.webapp.model.BookRequest>> activeByUser = new java.util.HashMap<>();
            java.util.Map<String, java.util.List<com.SecureLibrarySystem.webapp.model.BookRequest>> overdueByUser = new java.util.HashMap<>();
            for (String user : usernames) {
                activeByUser.put(user, bookRequestService.getActiveRequestsForUser(user));
                overdueByUser.put(user, bookRequestService.getOverdueRequestsForUser(user));
            }
            model.addAttribute("activeByUser", activeByUser);
            model.addAttribute("overdueByUser", overdueByUser);
        } else if ("STUDENT".equals(role)) {
            model.addAttribute("requests", bookRequestService.getRequestsForUser(username));
        } else {
            return "redirect:/dashboard";
        }

        model.addAttribute("role", role);
        return "book-requests";
    }

    @PostMapping("/create")
    public String createRequest(@RequestParam Long bookId, HttpSession session) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/dashboard";
        }

        String username = (String) session.getAttribute("username");
        BookRequestService.RequestCreateResult result = bookRequestService.createRequest(username, bookId);
        if (result == BookRequestService.RequestCreateResult.LIMIT_REACHED) {
            return "redirect:/books?requestLimit=true";
        }
        if (result == BookRequestService.RequestCreateResult.UNAVAILABLE) {
            return "redirect:/books?unavailable=true";
        }
        return "redirect:/books?requested=true";
    }

    @PostMapping("/accept/{id}")
    public String acceptRequest(
            @PathVariable Long id,
            @RequestParam int issueDays,
            HttpSession session) {

        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!("ADMIN".equals(role) || "LIBRARIAN".equals(role))) {
            return "redirect:/dashboard";
        }

        String username = (String) session.getAttribute("username");
        boolean accepted = bookRequestService.acceptRequest(id, username, issueDays);
        return accepted ? "redirect:/requests?accepted=true" : "redirect:/requests?unavailable=true";
    }

    @PostMapping("/reject/{id}")
    public String rejectRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            HttpSession session) {

        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!("ADMIN".equals(role) || "LIBRARIAN".equals(role))) {
            return "redirect:/dashboard";
        }

        String username = (String) session.getAttribute("username");
        boolean rejected = bookRequestService.rejectRequest(id, username, reason);
        return rejected ? "redirect:/requests?rejected=true" : "redirect:/requests?error=true";
    }
}
