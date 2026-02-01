package com.SecureLibrarySystem.webapp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SecureLibrarySystem.webapp.model.RestockRequest;

public interface RestockRequestDAO extends JpaRepository<RestockRequest, Long> {
    List<RestockRequest> findAllByOrderByRequestedAtDesc();
    List<RestockRequest> findByRequestedByOrderByRequestedAtDesc(String requestedBy);
}
