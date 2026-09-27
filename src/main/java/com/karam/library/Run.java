package com.karam.library;

import com.karam.library.exception.BookNotFoundException;
import com.karam.library.model.Book;
import com.karam.library.model.Member;
import com.karam.library.service.BookService;
import com.karam.library.service.BorrowingService;
import com.karam.library.service.MemberService;

import java.util.List;
import java.util.Scanner;

public class Run {

    private final Scanner sc = new Scanner(System.in);

    public void run(BookService bookService, MemberService memberService, BorrowingService borrowingService) {
        System.out.println("""
                ========================
                 Library Management
                ========================""");

        boolean running = true;
        while (running) {
            System.out.print("""
                    1. List books
                    2. Add book
                    3. Remove book
                    4. Search books
                    5. Register member
                    6. List members
                    7. Borrow book
                    8. Return book
                    9. View borrowed books
                    10. Exit
                    
                    Choose an option: """);

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.\n");
                continue;
            }

            switch (choice) {
                case 1 -> bookService.printBooks(bookService.findAll());
                case 2 -> addBook(bookService);
                case 3 -> removeBook(bookService);
                case 4 -> searchBooks(bookService);
                case 5 -> registerMember(memberService);
                case 6 -> memberService.printMembers(memberService.findAll());
                case 7 -> borrowBook(borrowingService);
                case 8 -> returnBook(borrowingService);
//                case 9 -> viewBorrowedBooks(borrowingService);
                case 10 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice");
            }
            System.out.println();
        }
    }

    private void addBook(BookService bookService) {
        try {
            System.out.print("Title: ");
            String title = sc.nextLine();

            System.out.print("Author: ");
            String author = sc.nextLine();

            System.out.print("ISBN: ");
            String isbn = sc.nextLine();

            System.out.print("Published year: ");
            int year = Integer.parseInt(sc.nextLine().trim());

            Book newBook = new Book();
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setIsbn(isbn);
            newBook.setPublishedYear(year);

            bookService.addBook(newBook);
            System.out.println("Book added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Published year must be a number.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Could not add book: " + e.getMessage());
        }
    }

    private void removeBook(BookService bookService) {
        try {
            System.out.print("Book ID to remove: ");
            long id = Long.parseLong(sc.nextLine().trim());

            bookService.deleteBook(id);
            System.out.println("Book removed successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchBooks(BookService bookService) {
        System.out.print("Title to search: ");
        String title = sc.nextLine();

        List<Book> results = bookService.searchByTitle(title);
        bookService.printBooks(results);
    }

    private void registerMember(MemberService memberService) {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            Member newMember = new Member();
            newMember.setName(name);
            newMember.setEmail(email);

            memberService.addMember(newMember);
            System.out.println("Member registered successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Could not register member: " + e.getMessage());
        }
    }

    private void borrowBook(BorrowingService borrowingService) {
        try {
            System.out.print("Book ID: ");
            long bookId = Long.parseLong(sc.nextLine().trim());

            System.out.print("Member ID: ");
            long memberId = Long.parseLong(sc.nextLine().trim());

            borrowingService.borrowBook(bookId, memberId);
            System.out.println("Book borrowed successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid IDs.");
        } catch (RuntimeException e) {
            System.out.println("Could not borrow book: " + e.getMessage());
        }
    }

    private void returnBook(BorrowingService borrowingService) {
        try {
            System.out.print("Book ID: ");
            long bookId = Long.parseLong(sc.nextLine().trim());

            borrowingService.returnBook(bookId);
            System.out.println("Book returned successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
        } catch (RuntimeException e) {
            System.out.println("Could not return book: " + e.getMessage());
        }
    }

    private void viewBorrowedBooks(BorrowingService borrowingService) {
        borrowingService.printBorrowings(borrowingService.findAllBorrowed());
    }
}