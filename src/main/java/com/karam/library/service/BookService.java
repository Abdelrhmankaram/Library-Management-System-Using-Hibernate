package com.karam.library.service;

import com.karam.library.dao.BookDao;
import com.karam.library.exception.BookNotFoundException;
import com.karam.library.model.Book;

import java.util.List;
import java.util.Optional;

public record BookService(BookDao bookDao) {

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }

        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("Book ISBN cannot be empty");
        }

        if (bookDao.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalStateException("A book with this ISBN already exists");
        }

        book.setAvailable(true);

        bookDao.save(book);
    }

    public Optional<Book> findById(Long id) {
        Optional<Book> books = bookDao.findById(id);
        if(books.isEmpty()) {
            throw new BookNotFoundException("Book with id " + id + " not found");
        }
        return books;
    }

    public List<Book> findAll() {
        return bookDao.findAll();
    }

    public void deleteBook(Long id) {
        if (bookDao.findById(id).isEmpty()) {
            throw new BookNotFoundException("Book with id " + id + " not found");
        }

        bookDao.delete(id);
    }

    public List<Book> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            return List.of();
        }

        return bookDao.searchByTitle(title);
    }

    public List<Book> findAvailableBooks() {
        return bookDao.findAvailableBooks();
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookDao.findByIsbn(isbn);
    }

    public void printBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("Books: (none found)");
            return;
        }

        String format = "%-5s %-25s %-20s %-15s %-6s %-10s%n";
        System.out.printf(format, "ID", "Title", "Author", "ISBN", "Year", "Available");
        System.out.println("-".repeat(85));

        for (Book book : books) {
            System.out.printf(format,
                    book.getId(),
                    truncate(book.getTitle(), 25),
                    truncate(book.getAuthor(), 20),
                    book.getIsbn(),
                    book.getPublishedYear(),
                    book.isAvailable());
        }
    }

    public void printBook(Optional<Book> book) {
        book.ifPresentOrElse(
                b -> printBooks(List.of(b)),
                () -> System.out.println("Book: not found")
        );
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() > maxLength ? value.substring(0, maxLength - 1) + "…" : value;
    }
}