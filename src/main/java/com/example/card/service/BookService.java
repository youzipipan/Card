package com.example.card.service;

import com.example.card.entities.Book;

import java.util.List;

public interface BookService {
    List<Book> findByAllStedentId(String studentId);

    List<Book> findByStedentIdAndFlag(String studentId, String flag);
}
