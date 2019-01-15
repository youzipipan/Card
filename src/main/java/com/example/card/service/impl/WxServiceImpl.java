package com.example.card.service.impl;

import com.example.card.entities.Student;
import com.example.card.repository.StudentRepository;
import com.example.card.service.WxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("wxService")
public class WxServiceImpl implements WxService {

    @Resource
    private StudentRepository studentRepository;

    @Transactional
    @Override
    public Student findByOpenId(String openId) {

        Student student = studentRepository.findByOpenId(openId);

        return student;
    }
}
