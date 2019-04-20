package com.example.card.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveModel {

    private String studentName;
    private String sex;//性别
    private String flag;//是否管理员
    private String department; //院系
    private String specialized;//专业
    private String grade;//年级
    private String studentClass;//班级
    private String studentNumber;//学号
    private String phone;//联系方式
    private String iDNumber;//身份证号
    private String passWord;//密码
    private String teacher;//导员

}
