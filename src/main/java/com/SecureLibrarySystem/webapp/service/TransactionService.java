package com.SecureLibrarySystem.webapp.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.dao.BookDAO;
import com.SecureLibrarySystem.webapp.dao.TransactionDAO;
import com.SecureLibrarySystem.webapp.model.Book;
import com.SecureLibrarySystem.webapp.model.Transaction;
import com.SecureLibrarySystem.webapp.util.DigitalSignatureUtil;

@Service
public class TransactionService {

    @Autowired
    private TransactionDAO transactionDAO;

    @Autowired
    private BookDAO bookDAO;

    // ======================
    // ISSUE BOOK (STUDENT)
    // ======================
    public void issueBook(String username, Long bookId) {

        Book book = bookDAO.findById(bookId).orElseThrow();

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDAO.save(book);

        record(username, book, "ISSUE");
    }

    // ======================
    // RETURN BOOK (STUDENT)
    // ======================
    public void returnBook(String username, Long bookId) {

        Book book = bookDAO.findById(bookId).orElseThrow();

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookDAO.save(book);

        record(username, book, "RETURN");
    }

    // ======================
    // RECORD TRANSACTION
    // ======================
    private void record(String username, Book book, String action) {

        Transaction tx = new Transaction();

        tx.setUsername(username);        // encrypted via AES converter
        tx.setBookId(book.getId());
        tx.setIsbn(book.getIsbn());      // encrypted via AES converter
        tx.setActionType(action);

        // 🔐 NORMALIZED TIMESTAMP (NO NANOSECONDS)
        LocalDateTime timestamp = LocalDateTime.now().withNano(0);
        tx.setTimestamp(timestamp);

        // 🔏 DATA TO SIGN (MUST MATCH VERIFY SIDE)
        String dataToSign =
                username + "|" +
                book.getId() + "|" +
                action + "|" +
                timestamp.toString();

        // ✍ DIGITAL SIGNATURE
        String signature = DigitalSignatureUtil.sign(dataToSign);
        tx.setDigitalSignature(signature);

        transactionDAO.save(tx);
    }
}
