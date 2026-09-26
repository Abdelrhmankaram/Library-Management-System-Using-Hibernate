package com.karam.library.dao;

import com.karam.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    void save(Book book);
    Optional<Book> findById(Long id);
    List<Book> findAll();
    void delete(Long id);
    List<Book> searchByTitle(String title);
    List<Book> findAvailableBooks();
    Optional<Book> findByIsbn(String isbn);
}
