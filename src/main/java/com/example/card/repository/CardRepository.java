package com.example.card.repository;

import com.example.card.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CardRepository extends JpaRepository<Card,String> {
    Card findByStudentId(String studentId);

    @Modifying
    void deleteByCardNumber(String cardNumber);

    @Modifying
    @Query("update Card c set c.balance=?1 where c.cardNumber=?2")
    void updateCard(String balance, String cardNumber);
}
