package com.SecureLibrarySystem.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.dao.BookAdditionRequestDAO;
import com.SecureLibrarySystem.webapp.dao.BookDAO;
import com.SecureLibrarySystem.webapp.model.Book;
import com.SecureLibrarySystem.webapp.model.BookAdditionRequest;
import com.SecureLibrarySystem.webapp.model.BookAdditionStatus;
import com.SecureLibrarySystem.webapp.model.NotificationType;

@Service
public class BookAdditionRequestService {

    @Autowired
    private BookAdditionRequestDAO requestDAO;

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private NotificationService notificationService;

    public void createRequest(BookAdditionRequest request) {
        requestDAO.save(request);
        String message = "New book request submitted: " + request.getTitle() + " by " + request.getRequestedBy() + ".";
        notificationService.createIfMissing(Role.ADMIN, NotificationType.NEW_BOOK_REQUEST, request.getId(), message);
    }

    public List<BookAdditionRequest> getAllRequests() {
        return requestDAO.findAllByOrderByRequestedAtDesc();
    }

    public List<BookAdditionRequest> getRequestsForUser(String username) {
        return requestDAO.findByRequestedByOrderByRequestedAtDesc(username);
    }

    public boolean approve(Long requestId, String approvedBy) {
        BookAdditionRequest request = requestDAO.findById(requestId).orElse(null);
        if (request == null || request.getStatus() != BookAdditionStatus.WAITING) {
            return false;
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        bookDAO.save(book);

        request.setStatus(BookAdditionStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now().withNano(0));
        request.setApprovedBy(approvedBy);
        requestDAO.save(request);
        return true;
    }
}
