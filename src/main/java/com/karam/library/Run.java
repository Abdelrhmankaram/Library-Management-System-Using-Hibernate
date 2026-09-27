package com.karam.library;

import com.karam.library.model.Book;
import com.karam.library.service.BookService;
import com.karam.library.service.BorrowingService;
import com.karam.library.service.MemberService;

import java.util.List;
import java.util.Scanner;

public class Run {
    public void run(BookService bookService, MemberService memberService, BorrowingService borrowingService){
        System.out.println("""
                ========================
                 Library Management
                ========================""");

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

        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();

        switch (choice) {
            case 1 -> {
                List<Book> booksFound = bookService.findAll();
                bookService.printBooks(booksFound);
            }
            case 2 -> {

            }
            case 3 -> {

            }
            case 4 -> {

            }
            case 5 -> {

            }
            case 6 -> {

            }
            case 7 -> {

            }
            case 8 -> {

            }
            case 9 -> {

            }
            case 10 -> {
                return;
            }
            default -> System.out.println("Invalid choice");
        }
    }
}
