package com.SecureLibrarySystem.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.SecureLibrarySystem.webapp.model.Book;
import com.SecureLibrarySystem.webapp.service.BookRequestService;
import com.SecureLibrarySystem.webapp.service.BookService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRequestService bookRequestService;

    /* VIEW BOOKS - ALL ROLES */
    @GetMapping
    public String viewBooks(Model model, HttpSession session) {

        String role = (String) session.getAttribute("role");

        model.addAttribute("role", role);
        model.addAttribute("books", bookService.getAllBooks());
        if ("STUDENT".equals(role)) {
            String username = (String) session.getAttribute("username");
            model.addAttribute("lastRequestAt", bookRequestService.getLastRequestTime(username));
            model.addAttribute("canRequest", bookRequestService.canRequest(username));
        }

        return "books";
    }

    /* ADD BOOK PAGE - ADMIN ONLY */
    @GetMapping("/add")
    public String addBookPage(HttpSession session) {

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }
        return "add-book";
    }

    /* ADD BOOK - ADMIN ONLY */
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book, HttpSession session) {

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        bookService.addBook(book);
        return "redirect:/books";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(
            @PathVariable Long id,
            HttpSession session) {

        String role = (String) session.getAttribute("role");

        // Controller-level protection
        if (!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        bookService.deleteBook(id, role);
        return "redirect:/books";
    }
}
