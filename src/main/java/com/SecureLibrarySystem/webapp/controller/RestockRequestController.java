package com.SecureLibrarySystem.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SecureLibrarySystem.webapp.dao.BookDAO;
import com.SecureLibrarySystem.webapp.model.Book;
import com.SecureLibrarySystem.webapp.model.RestockRequest;
import com.SecureLibrarySystem.webapp.service.RestockRequestService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/restock-requests")
public class RestockRequestController {

    @Autowired
    private RestockRequestService restockRequestService;

    @Autowired
    private BookDAO bookDAO;

    @GetMapping
    public String viewRequests(HttpSession session, Model model) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if ("ADMIN".equals(role)) {
            model.addAttribute("requests", restockRequestService.getAllRequests());
        } else if ("LIBRARIAN".equals(role)) {
            model.addAttribute("requests", restockRequestService.getRequestsForUser(username));
            List<Book> unavailableBooks = bookDAO.findByAvailableCopies(0);
            model.addAttribute("unavailableBooks", unavailableBooks);
        } else {
            return "redirect:/dashboard";
        }

        model.addAttribute("role", role);
        return "restock-requests";
    }

    @PostMapping("/create")
    public String createRequest(
            @RequestParam Long bookId,
            @RequestParam int quantity,
            @RequestParam String reason,
            HttpSession session) {

        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!"LIBRARIAN".equals(role)) {
            return "redirect:/dashboard";
        }

        Book book = bookDAO.findById(bookId).orElse(null);
        if (book == null || book.getAvailableCopies() > 0) {
            return "redirect:/restock-requests?invalid=true";
        }

        String username = (String) session.getAttribute("username");
        RestockRequest request = new RestockRequest();
        request.setBookId(book.getId());
        request.setBookTitle(book.getTitle());
        request.setBookIsbn(book.getIsbn());
        request.setRequestedBy(username);
        request.setQuantity(Math.max(1, quantity));
        request.setReason(reason);
        restockRequestService.createRequest(request);
        return "redirect:/restock-requests?submitted=true";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, HttpSession session) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        String username = (String) session.getAttribute("username");
        restockRequestService.approve(id, username);
        return "redirect:/restock-requests?approved=true";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, HttpSession session) {
        Boolean auth = (Boolean) session.getAttribute("AUTHENTICATED");
        if (auth == null || !auth) {
            return "redirect:/login-page";
        }

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        String username = (String) session.getAttribute("username");
        restockRequestService.reject(id, username);
        return "redirect:/restock-requests?rejected=true";
    }
}
