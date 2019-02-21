package com.example.card.service.impl;

import com.example.card.entities.Card;
import com.example.card.repository.CardRepository;
import com.example.card.service.CardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("cardService")
public class CardServiceImpl implements CardService {

    @Resource
    private CardRepository cardRepository;

    @Transactional
    @Override
    public Card findByStudentId(String studentId) {

        Card card = cardRepository.findByStudentId(studentId);
        return card;
    }
}
