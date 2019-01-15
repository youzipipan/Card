package com.example.card.repository;

import com.example.card.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,String> {

    Student findByOpenId(String openId);
}
