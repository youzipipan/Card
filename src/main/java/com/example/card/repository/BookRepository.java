package com.example.card.repository;

import com.example.card.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,String> {

    @Query("select b from Book b where b.studentId")
    List<Book> findByAllStedentId(String studentId);

    List<Book> findByStedentIdAndFlag(String studentId, String s);
}
