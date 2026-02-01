package com.SecureLibrarySystem.webapp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SecureLibrarySystem.webapp.model.BookAdditionRequest;

public interface BookAdditionRequestDAO extends JpaRepository<BookAdditionRequest, Long> {
    List<BookAdditionRequest> findAllByOrderByRequestedAtDesc();
    List<BookAdditionRequest> findByRequestedByOrderByRequestedAtDesc(String requestedBy);
}
