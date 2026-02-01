package com.SecureLibrarySystem.webapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.dao.BookDAO;
import com.SecureLibrarySystem.webapp.dao.BookRequestDAO;
import com.SecureLibrarySystem.webapp.model.Book;
import com.SecureLibrarySystem.webapp.model.BookRequest;
import com.SecureLibrarySystem.webapp.model.NotificationType;
import com.SecureLibrarySystem.webapp.model.RequestStatus;

import java.time.Duration;
import java.util.Collections;

@Service
public class BookRequestService {

    @Autowired
    private BookRequestDAO bookRequestDAO;

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private NotificationService notificationService;

    public RequestCreateResult createRequest(String username, Long bookId) {
        if (!canRequest(username)) {
            return RequestCreateResult.LIMIT_REACHED;
        }
        Book book = bookDAO.findById(bookId).orElseThrow();
        if (book.getAvailableCopies() <= 0) {
            String message = "Book unavailable for request: " + book.getTitle() + " (ISBN " + book.getIsbn() + ").";
            notificationService.createIfMissing(Role.LIBRARIAN, NotificationType.BOOK_UNAVAILABLE, book.getId(), message);
            return RequestCreateResult.UNAVAILABLE;
        }
        BookRequest request = new BookRequest();
        request.setUsername(username);
        request.setBookId(bookId);
        request.setBookTitle(book.getTitle());
        request.setStatus(RequestStatus.WAITING);
        bookRequestDAO.save(request);
        return RequestCreateResult.CREATED;
    }

    public List<BookRequest> getAllRequests() {
        return bookRequestDAO.findAllByOrderByRequestedAtDesc();
    }

    public List<BookRequest> getRequestsForUser(String username) {
        return bookRequestDAO.findByUsernameOrderByRequestedAtDesc(username);
    }

    public List<BookRequest> getActiveRequestsForUser(String username) {
        if (username == null) {
            return Collections.emptyList();
        }
        return bookRequestDAO.findByUsernameAndStatusAndReturnedAtIsNullOrderByAcceptedAtDesc(
                username, RequestStatus.ACCEPTED);
    }

    public List<BookRequest> getOverdueRequestsForUser(String username) {
        if (username == null) {
            return Collections.emptyList();
        }
        return bookRequestDAO.findByUsernameAndStatusAndDueDateBeforeAndReturnedAtIsNull(
                username, RequestStatus.ACCEPTED, LocalDate.now());
    }

    public boolean canRequest(String username) {
        if (username == null) {
            return false;
        }
        return bookRequestDAO.findTopByUsernameOrderByRequestedAtDesc(username)
                .map(last -> Duration.between(last.getRequestedAt(), LocalDateTime.now()).toHours() >= 24)
                .orElse(true);
    }

    public LocalDateTime getLastRequestTime(String username) {
        if (username == null) {
            return null;
        }
        return bookRequestDAO.findTopByUsernameOrderByRequestedAtDesc(username)
                .map(BookRequest::getRequestedAt)
                .orElse(null);
    }

    public boolean acceptRequest(Long requestId, String approvedBy, int issueDays) {
        BookRequest request = bookRequestDAO.findById(requestId).orElse(null);
        if (request == null || request.getStatus() != RequestStatus.WAITING) {
            return false;
        }

        if (issueDays <= 0) {
            issueDays = 7;
        }

        Book book = bookDAO.findById(request.getBookId()).orElse(null);
        if (book == null || book.getAvailableCopies() <= 0) {
            String message = "Book unavailable for request " + request.getId() +
                    " (" + request.getBookTitle() + "). Please consider procurement.";
            notificationService.createIfMissing(Role.LIBRARIAN, NotificationType.BOOK_UNAVAILABLE, request.getId(), message);
            return false;
        }

        request.setAcceptedAt(LocalDateTime.now().withNano(0));
        request.setIssueDays(issueDays);
        request.setDueDate(LocalDate.now().plusDays(issueDays));
        request.setStatus(RequestStatus.ACCEPTED);
        request.setApprovedBy(approvedBy);
        bookRequestDAO.save(request);

        transactionService.issueBook(request.getUsername(), request.getBookId());
        String message = "Your request for \"" + request.getBookTitle() + "\" was approved. Due date: " + request.getDueDate() + ".";
        notificationService.createForUser(request.getUsername(), NotificationType.NEW_BOOK_REQUEST, request.getId(), message);
        return true;
    }

    public boolean rejectRequest(Long requestId, String rejectedBy, String reason) {
        BookRequest request = bookRequestDAO.findById(requestId).orElse(null);
        if (request == null || request.getStatus() != RequestStatus.WAITING) {
            return false;
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setApprovedBy(rejectedBy);
        bookRequestDAO.save(request);

        String message = "Your request for \"" + request.getBookTitle() + "\" was rejected due to pending returns.";
        if (reason != null && !reason.isBlank()) {
            message = message + " Reason: " + reason;
        }
        notificationService.createForUser(request.getUsername(), NotificationType.NEW_BOOK_REQUEST, request.getId(), message);
        return true;
    }

    public void handleReturn(String username, Long bookId) {
        Optional<BookRequest> request = bookRequestDAO
                .findTopByUsernameAndBookIdAndStatusOrderByAcceptedAtDesc(username, bookId, RequestStatus.ACCEPTED);
        if (request.isPresent()) {
            BookRequest req = request.get();
            req.setReturnedAt(LocalDateTime.now().withNano(0));
            req.setStatus(RequestStatus.RETURNED);
            bookRequestDAO.save(req);
            transactionService.returnBook(username, bookId);
        }
    }

    public enum RequestCreateResult {
        CREATED,
        UNAVAILABLE,
        LIMIT_REACHED
    }

    public void generateOverdueNotifications() {
        List<BookRequest> overdue = bookRequestDAO
                .findByStatusAndDueDateBeforeAndReturnedAtIsNull(RequestStatus.ACCEPTED, LocalDate.now());
        for (BookRequest request : overdue) {
            String message = "Overdue: " + request.getBookTitle() + " requested by " + request.getUsername() +
                    " (due " + request.getDueDate() + ").";
            notificationService.createIfMissing(Role.ADMIN, NotificationType.OVERDUE, request.getId(), message);
            notificationService.createIfMissing(Role.LIBRARIAN, NotificationType.OVERDUE, request.getId(), message);
        }
    }
}
