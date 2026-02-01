package com.SecureLibrarySystem.webapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SecureLibrarySystem.webapp.model.Transaction;

import java.util.List;

public interface TransactionDAO extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUsername(String username);
}
