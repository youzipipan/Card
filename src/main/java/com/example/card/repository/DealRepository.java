package com.example.card.repository;

import com.example.card.entities.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal,String> {
    List<Deal> findByStudentId(String studentId);
}
