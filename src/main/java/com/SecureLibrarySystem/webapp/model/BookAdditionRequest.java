package com.SecureLibrarySystem.webapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_addition_requests")
public class BookAdditionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestedBy;

    private String title;

    private String author;

    private String isbn;

    private String genre;

    private int totalCopies;

    @Enumerated(EnumType.STRING)
    private BookAdditionStatus status = BookAdditionStatus.WAITING;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private String approvedBy;

    @PrePersist
    public void onCreate() {
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now().withNano(0);
        } else {
            this.requestedAt = this.requestedAt.withNano(0);
        }
    }

    public Long getId() {
        return id;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public BookAdditionStatus getStatus() {
        return status;
    }

    public void setStatus(BookAdditionStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
}
