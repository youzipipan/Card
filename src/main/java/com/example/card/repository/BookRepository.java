package com.example.card.repository;

import com.example.card.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,String> {

    List<Book> findAllByStudentId(String studentId);

    List<Book> findByStudentIdAndFlag(String studentId, String s);
}
