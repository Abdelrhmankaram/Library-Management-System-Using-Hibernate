package com.karam.library.dao;

import com.karam.library.model.Borrowing;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowingDao {
    void save(Borrowing borrowing);
    Optional<Borrowing> findById(Long id);
    Optional<Borrowing> findActiveByBookId(Long bookId);
    List<Borrowing> findAllByMemberId(Long memberId);
    List<Borrowing> findAllActive();
    List<Borrowing> findOverdue(LocalDate cutoffDate);
    void update(Borrowing borrowing);

    List<Borrowing> findAllBorrowed();
}
