package com.karam.library.dao.impl;

import com.karam.library.dao.BorrowingDao;
import com.karam.library.model.Borrowing;
import com.karam.library.util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BorrowingDaoImpl implements BorrowingDao {

    @Override
    public void save(Borrowing borrowing) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            session.persist(borrowing);

            session.getTransaction().commit();
        }
    }

    @Override
    public Optional<Borrowing> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(
                    session.find(Borrowing.class, id)
            );
        }
    }

    @Override
    public Optional<Borrowing> findActiveByBookId(Long bookId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Borrowing borrowing = session.createQuery(
                            """
                            FROM Borrowing b
                            WHERE b.book.id = :bookId
                            AND b.returnDate IS NULL
                            """,
                            Borrowing.class
                    )
                    .setParameter("bookId", bookId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            return Optional.ofNullable(borrowing);
        }
    }

    @Override
    public List<Borrowing> findAllByMemberId(Long memberId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                            """
                            FROM Borrowing b
                            WHERE b.member.id = :memberId
                            ORDER BY b.borrowDate DESC
                            """,
                            Borrowing.class
                    )
                    .setParameter("memberId", memberId)
                    .getResultList();
        }
    }

    @Override
    public List<Borrowing> findAllActive() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                            """
                            FROM Borrowing b
                            WHERE b.returnDate IS NULL
                            ORDER BY b.borrowDate DESC
                            """,
                            Borrowing.class
                    )
                    .getResultList();
        }
    }

    @Override
    public List<Borrowing> findOverdue(LocalDate cutoffDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                            """
                            FROM Borrowing b
                            WHERE b.returnDate IS NULL
                            AND b.borrowDate < :cutoffDate
                            ORDER BY b.borrowDate ASC
                            """,
                            Borrowing.class
                    )
                    .setParameter("cutoffDate", cutoffDate)
                    .getResultList();
        }
    }

    @Override
    public void update(Borrowing borrowing) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            session.merge(borrowing);

            session.getTransaction().commit();
        }
    }

    @Override
    public List<Borrowing> findAllBorrowed() {
        return List.of();
    }
}