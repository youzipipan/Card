package com.example.card.service;

import com.example.card.entities.Card;

public interface CardService {
    Card findByStudentId(String studentId);
}
