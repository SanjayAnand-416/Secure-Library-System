package com.SecureLibrarySystem.webapp.model;

import jakarta.persistence.*;
import com.SecureLibrarySystem.webapp.crypto.AESEncryptConverter;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = AESEncryptConverter.class)
    private String title;
    
    @Convert(converter = AESEncryptConverter.class)
    private String author;

    @Convert(converter = AESEncryptConverter.class)
    private String isbn;   // 🔐 encrypted automatically

    @Convert(converter = AESEncryptConverter.class)
    private String genre;

    private int totalCopies;
    private int availableCopies;

    // getters & setters
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }


    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}
