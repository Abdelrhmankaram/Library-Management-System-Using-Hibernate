package com.karam.library;

import java.util.Map;
import java.util.Scanner;

import com.karam.library.dao.MemberDao;
import com.karam.library.dao.impl.MemberDaoImpl;
import com.karam.library.service.MemberService;

import com.karam.library.dao.BookDao;
import com.karam.library.dao.impl.BookDaoImpl;
import com.karam.library.service.BookService;

import com.karam.library.dao.BorrowingDao;
import com.karam.library.dao.impl.BorrowingDaoImpl;
import com.karam.library.service.BorrowingService;

public class Main {
    public static void main(String[] args) {
        Run run = new Run();

        BookDao bookDao = new BookDaoImpl();
        BookService bookService = new BookService(bookDao);

        MemberDao memberDao = new MemberDaoImpl();
        MemberService memberService = new MemberService(memberDao);

        BorrowingDao borrowingDao = new BorrowingDaoImpl();
        BorrowingService borrowingService = new BorrowingService(borrowingDao, bookDao, memberDao);

        run.run(bookService, memberService, borrowingService);
    }
}