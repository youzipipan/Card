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
@Table(name="t_deal")
@AllArgsConstructor
@NoArgsConstructor
public class Deal {

    @Id
    private String UUID;
    @Column
    private String studentId;// 学生ID
    @Column
    private String orderNo;//交易号
    @Column
    private String price;//交易金额
    @Column
    private String dealTime;//交易时间
    @Column
    private String state;//标志

}
