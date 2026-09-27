package com.karam.library;

import com.karam.library.dao.*;
import com.karam.library.dao.impl.*;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        BookDao bookDao = new BookDaoImpl();

        System.out.println(bookDao.findByIsbn("97801323508"));
    }
}