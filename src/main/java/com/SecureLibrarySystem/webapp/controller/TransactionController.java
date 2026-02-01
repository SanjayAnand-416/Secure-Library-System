package com.SecureLibrarySystem.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.SecureLibrarySystem.webapp.service.TransactionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/issue")
    public String issueBook(
            @RequestParam Long bookId,
            HttpSession session) {

        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");

        // ✅ ONLY STUDENT can issue
        if (!"STUDENT".equals(role)) {
            return "redirect:/dashboard?denied=true";
        }

        transactionService.issueBook(username, bookId);
        return "redirect:/books?issued=true";
    }

    @PostMapping("/return")
    public String returnBook(
            @RequestParam Long bookId,
            HttpSession session) {

        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");

        // ✅ ONLY STUDENT can return
        if (!"STUDENT".equals(role)) {
            return "redirect:/dashboard?denied=true";
        }

        transactionService.returnBook(username, bookId);
        return "redirect:/books?returned=true";
    }
}
