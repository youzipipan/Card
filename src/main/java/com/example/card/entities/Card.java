package com.example.card.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="t_card")
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    private String UUID;
    @Column
    private String studentId;// 学生ID
    @Column
    private String cardNumber;//一卡通卡号
    @Column
    private String balance; //余额
    @Column
    private String integral;//积分
}
