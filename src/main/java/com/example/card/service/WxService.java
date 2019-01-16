package com.example.card.service;

import com.example.card.entities.Student;

public interface WxService {
    Student findByOpenId(String openId);

    Student findByCardNumberAndPassWord(String cardNumber, String password);

    int updateByOpenId(String cardNumber, String passWord, String openId);
}
