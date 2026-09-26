package com.karam.library.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @Column
    private LocalDate borrowDate;

    @Column
    private LocalDate returnDate;

    public long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
