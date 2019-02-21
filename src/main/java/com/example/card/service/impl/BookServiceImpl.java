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
    public List<Book> findAllByStudentId(String studentId) {

        List<Book> bookList = bookRepository.findAllByStudentId(studentId);
        return bookList;
    }

    @Transactional
    @Override
    public List<Book> findByStudentIdAndFlag(String studentId ,String flag) {

        List<Book> bookList = bookRepository.findByStudentIdAndFlag(studentId,flag);
        return bookList;
    }

}
