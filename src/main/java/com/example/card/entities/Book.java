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
@Table(name="t_book")
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    private String UUID;
    @Column
    private String studentId;// 学生ID
    @Column
    private String flag;//是否还书  1已还书  0未还书
    @Column
    private String title; //书名
    @Column
    private String returnTime;//还书时间
    @Column
    private String borrowTime;//借书时间
    @Column
    private String days;//天数
    @Column
    private String money;//金额

}
