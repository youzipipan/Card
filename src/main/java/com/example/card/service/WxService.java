package com.example.card.service;

import com.example.card.entities.Card;
import com.example.card.entities.Student;
import com.example.card.model.SaveModel;

import java.util.List;

public interface WxService {
    Student findByOpenId(String openId);

    Student findByCardNumberAndPassWord(String cardNumber, String password);

    int updateByOpenId(String cardNumber, String passWord, String openId);

    Student findByStudentId(String studentId);

    String save(SaveModel saveModel);

    Student query(String cardNumber);

    List<Student> getAllStudent();
}
