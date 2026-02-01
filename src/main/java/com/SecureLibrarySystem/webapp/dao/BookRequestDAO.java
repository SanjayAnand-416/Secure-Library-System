package com.SecureLibrarySystem.webapp.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SecureLibrarySystem.webapp.model.BookRequest;
import com.SecureLibrarySystem.webapp.model.RequestStatus;

public interface BookRequestDAO extends JpaRepository<BookRequest, Long> {
    List<BookRequest> findAllByOrderByRequestedAtDesc();
    List<BookRequest> findByUsernameOrderByRequestedAtDesc(String username);
    java.util.Optional<BookRequest> findTopByUsernameOrderByRequestedAtDesc(String username);
    List<BookRequest> findByUsernameAndStatusAndReturnedAtIsNullOrderByAcceptedAtDesc(String username, RequestStatus status);
    List<BookRequest> findByUsernameAndStatusAndDueDateBeforeAndReturnedAtIsNull(String username, RequestStatus status, LocalDate dueDate);
    List<BookRequest> findByStatusAndDueDateBeforeAndReturnedAtIsNull(RequestStatus status, LocalDate dueDate);
    Optional<BookRequest> findTopByUsernameAndBookIdAndStatusOrderByAcceptedAtDesc(
            String username,
            Long bookId,
            RequestStatus status);
}
