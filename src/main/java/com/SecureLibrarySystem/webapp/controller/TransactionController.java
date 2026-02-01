package com.SecureLibrarySystem.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SecureLibrarySystem.webapp.service.BookRequestService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private BookRequestService bookRequestService;

    @PostMapping("/issue")
    public String issueBook(@RequestParam Long bookId, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/dashboard?denied=true";
        }

        // Students must request instead of direct issue.
        return "redirect:/books?requestOnly=true";
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam Long bookId, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");

        if (!"STUDENT".equals(role)) {
            return "redirect:/dashboard?denied=true";
        }

        bookRequestService.handleReturn(username, bookId);
        return "redirect:/books?returned=true";
    }
}
