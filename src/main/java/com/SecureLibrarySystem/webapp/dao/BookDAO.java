package com.SecureLibrarySystem.webapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SecureLibrarySystem.webapp.model.Book;

public interface BookDAO extends JpaRepository<Book, Long> {
}
