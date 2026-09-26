package com.karam.library.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String author;

    @Column
    private String isbn;

    @Column(name = "published_year")
    private int publishedYear;

    @Column
    private boolean available;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPublished_year() {
        return publishedYear;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPublished_year(int published_year) {
        this.publishedYear = published_year;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
