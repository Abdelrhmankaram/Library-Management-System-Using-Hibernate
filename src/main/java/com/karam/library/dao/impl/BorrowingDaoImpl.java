package com.karam.library.dao.impl;

import com.karam.library.dao.BorrowingDao;
import com.karam.library.model.Borrowing;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BorrowingDaoImpl implements BorrowingDao {

    @Override
    public void save(Borrowing borrowing) {

    }

    @Override
    public Optional<Borrowing> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Borrowing> findActiveByBookId(Long bookId) {
        return Optional.empty();
    }

    @Override
    public List<Borrowing> findAllByMemberId(Long memberId) {
        return List.of();
    }

    @Override
    public List<Borrowing> findAllActive() {
        return List.of();
    }

    @Override
    public List<Borrowing> findOverdue(LocalDate cutoffDate) {
        return List.of();
    }
}
