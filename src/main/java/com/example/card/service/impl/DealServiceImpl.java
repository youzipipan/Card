package com.example.card.service.impl;

import com.example.card.entities.Deal;
import com.example.card.repository.DealRepository;
import com.example.card.service.DealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("dealService")
public class DealServiceImpl implements DealService {

    @Resource
    private DealRepository dealRepository;

    @Transactional
    @Override
    public List<Deal> findByStudentId(String studentId) {

        List<Deal> dealList = dealRepository.findByStudentId(studentId);
        return dealList;
    }
}
