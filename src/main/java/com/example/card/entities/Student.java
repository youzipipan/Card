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
@Table(name="t_student")
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    private String studentId;
    @Column
    private String openId;//
    @Column
    private String flag;//是否管理员
    @Column
    private String department; //院系
    @Column
    private String specialized;//专业
    @Column
    private String grade;//年级
    @Column
    private String studentClass;//班级
    @Column
    private String studentNumber;//学号
    @Column
    private String name;//姓名
    @Column
    private String sex;//性别
    @Column
    private String phone;//联系方式
    @Column
    private String idnumber;//身份证号
    @Column
    private String cardNumber;//一卡通卡号
    @Column
    private String passWord;//密码
    @Column
    private String teacher;//导员


}
