package com.karam.library;

import com.karam.library.model.Book;
import com.karam.library.model.Borrowing;
import com.karam.library.model.Member;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        // Create Hibernate SessionFactory
        SessionFactory factory = new Configuration()
                .configure()
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Member.class)
                .addAnnotatedClass(Borrowing.class)
                .buildSessionFactory();

        try (factory) {

            // ==========================================
            // 1. CREATE BOOK
            // ==========================================

            Book book = new Book();

            book.setTitle("Clean Code");
            book.setAuthor("Robert C. Martin");
            book.setIsbn("9780132350884");
            book.setPublishedYear(2008);
            book.setAvailable(true);


            // ==========================================
            // 2. CREATE MEMBER
            // ==========================================

            Member member = new Member();

            member.setName("Abdelrahman");
            member.setEmail("abdelrahman@example.com");


            // ==========================================
            // 3. SAVE BOOK AND MEMBER
            // ==========================================

            Session session = factory.openSession();

            session.beginTransaction();

            session.persist(book);
            session.persist(member);

            session.getTransaction().commit();
            session.close();


            System.out.println("Book ID: " + book.getId());
            System.out.println("Member ID: " + member.getId());


            // ==========================================
            // 4. BORROW THE BOOK
            // ==========================================

            Borrowing borrowing = new Borrowing();

            borrowing.setBook(book);
            borrowing.setMember(member);
            borrowing.setBorrowDate(LocalDate.now());

            // null means the book hasn't been returned
            borrowing.setReturnDate(null);


            session = factory.openSession();

            session.beginTransaction();

            session.persist(borrowing);

            // The book is no longer available
            book.setAvailable(false);
            session.merge(book);

            session.getTransaction().commit();
            session.close();


            System.out.println("Borrowing ID: " + borrowing.getId());
            System.out.println("Book borrowed successfully.");


            // ==========================================
            // 5. RETRIEVE THE BORROWING
            // ==========================================

            session = factory.openSession();

            Borrowing savedBorrowing =
                    session.find(Borrowing.class, borrowing.getId());

            System.out.println("\n--- Borrowing Information ---");
            System.out.println("Borrowing ID: " + savedBorrowing.getId());
            System.out.println("Book: " + savedBorrowing.getBook().getTitle());
            System.out.println("Member: " + savedBorrowing.getMember().getName());
            System.out.println("Borrow Date: " + savedBorrowing.getBorrowDate());
            System.out.println("Return Date: " + savedBorrowing.getReturnDate());

            session.close();


            // ==========================================
            // 6. RETURN THE BOOK
            // ==========================================

            session = factory.openSession();

            session.beginTransaction();

            Borrowing borrowingToReturn =
                    session.get(Borrowing.class, borrowing.getId());

            borrowingToReturn.setReturnDate(LocalDate.now());

            Book returnedBook = borrowingToReturn.getBook();
            returnedBook.setAvailable(true);

            session.merge(returnedBook);

            session.getTransaction().commit();
            session.close();


            System.out.println("\nBook returned successfully.");
            System.out.println("Return Date: " +
                    borrowingToReturn.getReturnDate());


            // ==========================================
            // 7. VERIFY BOOK AVAILABILITY
            // ==========================================

            session = factory.openSession();

            Book savedBook =
                    session.get(Book.class, book.getId());

            System.out.println("\n--- Book Information ---");
            System.out.println("ID: " + savedBook.getId());
            System.out.println("Title: " + savedBook.getTitle());
            System.out.println("Author: " + savedBook.getAuthor());
            System.out.println("ISBN: " + savedBook.getIsbn());
            System.out.println("Published Year: " + savedBook.getPublishedYear());
            System.out.println("Available: " + savedBook.isAvailable());

            session.close();
        }
    }
}