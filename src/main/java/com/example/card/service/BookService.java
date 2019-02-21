package com.example.card.service;

import com.example.card.entities.Book;

import java.util.List;

public interface BookService {
    List<Book> findAllByStudentId(String studentId);

    List<Book> findByStudentIdAndFlag(String studentId, String flag);
}
