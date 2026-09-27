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
}