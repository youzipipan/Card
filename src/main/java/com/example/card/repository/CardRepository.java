package com.example.card.repository;

import com.example.card.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card,String> {
}
