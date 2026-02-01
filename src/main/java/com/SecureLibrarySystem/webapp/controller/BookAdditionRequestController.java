package com.SecureLibrarySystem.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.SecureLibrarySystem.webapp.model.BookAdditionRequest;
import com.SecureLibrarySystem.webapp.service.BookAdditionRequestService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/book-add-requests")
public class BookAdditionRequestController {

    @Autowired
    private BookAdditionRequestService requestService;

    @GetMapping
    public String viewRequests(HttpSession session, Model model) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if ("ADMIN".equals(role)) {
            model.addAttribute("requests", requestService.getAllRequests());
        } else if ("LIBRARIAN".equals(role)) {
            model.addAttribute("requests", requestService.getRequestsForUser(username));
        } else {
            return "redirect:/dashboard";
        }

        model.addAttribute("role", role);
        return "book-add-requests";
    }

    @PostMapping("/create")
    public String createRequest(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam String isbn,
            @RequestParam String genre,
            @RequestParam int totalCopies,
            HttpSession session) {

        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!"LIBRARIAN".equals(role)) {
            return "redirect:/dashboard";
        }

        String username = (String) session.getAttribute("username");
        BookAdditionRequest request = new BookAdditionRequest();
        request.setRequestedBy(username);
        request.setTitle(title);
        request.setAuthor(author);
        request.setIsbn(isbn);
        request.setGenre(genre);
        request.setTotalCopies(totalCopies);
        requestService.createRequest(request);
        return "redirect:/book-add-requests?submitted=true";
    }

    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable Long id, HttpSession session) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        String username = (String) session.getAttribute("username");
        requestService.approve(id, username);
        return "redirect:/book-add-requests?approved=true";
    }
}
