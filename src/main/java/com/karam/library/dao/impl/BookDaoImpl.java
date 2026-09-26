package com.karam.library.dao.impl;

import com.karam.library.dao.BookDao;
import com.karam.library.model.Book;

import java.util.List;
import java.util.Optional;

public class BookDaoImpl implements BookDao {


    @Override
    public void save(Book book) {

    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Book> searchByTitle(String title) {
        return List.of();
    }

    @Override
    public List<Book> findAvailableBooks() {
        return List.of();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.empty();
    }
}
