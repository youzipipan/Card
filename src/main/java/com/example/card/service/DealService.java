package com.example.card.service;

import com.example.card.entities.Deal;

import java.util.List;

public interface DealService {
    List<Deal> findByStudentId(String studentId);
}
