package com.example.card.service.impl;

import com.example.card.entities.Book;
import com.example.card.repository.BookRepository;
import com.example.card.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("bookService")
public class BookServiceImpl implements BookService {

    @Resource
    private BookRepository bookRepository;

    @Transactional
    @Override
    public List<Book> findByAllStedentId(String studentId) {

        List<Book> bookList = bookRepository.findByAllStedentId(studentId);
        return bookList;
    }

    @Transactional
    @Override
    public List<Book> findByStedentIdAndFlag(String studentId ,String flag) {

        List<Book> bookList = bookRepository.findByStedentIdAndFlag(studentId,flag);
        return bookList;
    }

}
