package com.example.card.service.impl;

import com.example.card.entities.Room;
import com.example.card.repository.RoomRepository;
import com.example.card.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("roomService")
public class RoomServiceImpl implements RoomService {

    @Resource
    private RoomRepository roomRepository;

    @Transactional
    @Override
    public List<Room> findByStudentId(String studentId, String s) {

        List<Room> roomList = roomRepository.findByStudentIdAndFlag(studentId,s);
        return roomList;
    }
}
