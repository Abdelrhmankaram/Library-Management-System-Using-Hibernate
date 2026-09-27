package com.karam.library.service;

import com.karam.library.dao.BookDao;
import com.karam.library.dao.BorrowingDao;
import com.karam.library.dao.MemberDao;
import com.karam.library.exception.BookNotFoundException;
import com.karam.library.exception.BookUnavailableException;
import com.karam.library.exception.MemberNotFoundException;
import com.karam.library.model.Book;
import com.karam.library.model.Borrowing;
import com.karam.library.model.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record BorrowingService(BorrowingDao borrowingDao, BookDao bookDao, MemberDao memberDao) {

    public void borrowBook(Long bookId, Long memberId) {

        Book book = bookDao.findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException("Book with id " + bookId + " not found")
                );

        Member member = memberDao.findById(memberId)
                .orElseThrow(() ->
                        new MemberNotFoundException("Member with id " + memberId + " not found")
                );

        if (!book.isAvailable()) {
            throw new BookUnavailableException("Book with id " + bookId + " is not available");
        }

        if (borrowingDao.findActiveByBookId(bookId).isPresent()) {
            throw new IllegalStateException(
                    "Book already has an active borrowing"
            );
        }

        Borrowing borrowing = new Borrowing();

        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setReturnDate(null);

        book.setAvailable(false);

        borrowingDao.save(borrowing);
    }

    public void returnBook(Long borrowingId) {

        Borrowing borrowing = borrowingDao.findById(borrowingId)
                .orElseThrow(() ->
                        new BookNotFoundException("Book with id " + borrowingId + " not found")
                );

        if (borrowing.getReturnDate() != null) {
            throw new IllegalStateException(
                    "This book has already been returned"
            );
        }

        borrowing.setReturnDate(LocalDate.now());

        Book book = borrowing.getBook();
        book.setAvailable(true);

        borrowingDao.update(borrowing);
    }

    public Optional<Borrowing> findById(Long id) {
        return borrowingDao.findById(id);
    }

    public Optional<Borrowing> findActiveByBookId(Long bookId) {
        return borrowingDao.findActiveByBookId(bookId);
    }

    public List<Borrowing> findAllByMemberId(Long memberId) {
        return borrowingDao.findAllByMemberId(memberId);
    }

    public List<Borrowing> findAllActive() {
        return borrowingDao.findAllActive();
    }

    public List<Borrowing> findOverdue(LocalDate cutoffDate) {
        return borrowingDao.findOverdue(cutoffDate);
    }
}