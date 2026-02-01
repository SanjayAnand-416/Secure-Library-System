package com.SecureLibrarySystem.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.dao.BookDAO;
import com.SecureLibrarySystem.webapp.model.Book;

@Service
public class BookService {

    @Autowired
    private BookDAO bookDAO;

    public void addBook(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        bookDAO.save(book);
    }

    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }
    public void deleteBook(Long bookId, String role) {

        // Service-level protection (cannot be bypassed)
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access Denied");
        }

        bookDAO.deleteById(bookId);
    }

}
