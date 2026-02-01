package com.SecureLibrarySystem.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.dao.BookDAO;
import com.SecureLibrarySystem.webapp.dao.RestockRequestDAO;
import com.SecureLibrarySystem.webapp.model.Book;
import com.SecureLibrarySystem.webapp.model.RestockRequest;
import com.SecureLibrarySystem.webapp.model.RestockRequestStatus;

@Service
public class RestockRequestService {

    @Autowired
    private RestockRequestDAO restockRequestDAO;

    @Autowired
    private BookDAO bookDAO;

    public void createRequest(RestockRequest request) {
        restockRequestDAO.save(request);
    }

    public List<RestockRequest> getAllRequests() {
        return restockRequestDAO.findAllByOrderByRequestedAtDesc();
    }

    public List<RestockRequest> getRequestsForUser(String username) {
        return restockRequestDAO.findByRequestedByOrderByRequestedAtDesc(username);
    }

    public boolean approve(Long requestId, String decidedBy) {
        RestockRequest request = restockRequestDAO.findById(requestId).orElse(null);
        if (request == null || request.getStatus() != RestockRequestStatus.WAITING) {
            return false;
        }

        Book book = bookDAO.findById(request.getBookId()).orElse(null);
        if (book == null) {
            return false;
        }

        int qty = Math.max(1, request.getQuantity());
        book.setTotalCopies(book.getTotalCopies() + qty);
        book.setAvailableCopies(book.getAvailableCopies() + qty);
        bookDAO.save(book);

        request.setStatus(RestockRequestStatus.APPROVED);
        request.setDecidedAt(LocalDateTime.now().withNano(0));
        request.setDecidedBy(decidedBy);
        restockRequestDAO.save(request);
        return true;
    }

    public boolean reject(Long requestId, String decidedBy) {
        RestockRequest request = restockRequestDAO.findById(requestId).orElse(null);
        if (request == null || request.getStatus() != RestockRequestStatus.WAITING) {
            return false;
        }

        request.setStatus(RestockRequestStatus.REJECTED);
        request.setDecidedAt(LocalDateTime.now().withNano(0));
        request.setDecidedBy(decidedBy);
        restockRequestDAO.save(request);
        return true;
    }
}
