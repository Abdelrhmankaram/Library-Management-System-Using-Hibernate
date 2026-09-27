package com.karam.library.dao.impl;

import com.karam.library.dao.BookDao;
import com.karam.library.model.Book;
import com.karam.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class BookDaoImpl implements BookDao {
    @Override
    public void save(Book book) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(book);
            session.getTransaction().commit();
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Book.class, id));
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> book = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            book = session.createQuery("FROM Book", Book.class).getResultList();
        }
        return book;
    }

    @Override
    public void delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Book bookToBeRemoved = session.find(Book.class, id);

            if (bookToBeRemoved != null) {
                session.remove(bookToBeRemoved);
            }

            session.getTransaction().commit();
        }
    }

    @Override
    public List<Book> searchByTitle(String title) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Book WHERE title LIKE :title",
                            Book.class
                    )
                    .setParameter("title", "%" + title + "%")
                    .getResultList();
        }
    }

    @Override
    public List<Book> findAvailableBooks() {
        List<Book> books = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            books = session.createQuery("FROM Book WHERE available = true", Book.class)
                    .getResultList();
        }
        return books;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                            "FROM Book WHERE isbn LIKE :isbn",
                            Book.class
                    )
                    .setParameter("isbn", "%" + isbn + "%")
                    .uniqueResultOptional();
        }
    }
}
