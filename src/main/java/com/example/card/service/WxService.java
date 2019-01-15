package com.example.card.service;

import com.example.card.entities.Student;

public interface WxService {
    Student findByOpenId(String openId);
}
