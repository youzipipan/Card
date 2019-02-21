package com.example.card.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardModel {

    private String studentName;
    private String phone;
    private String cardNumber;
    private String balance;
    private String integral;
}
