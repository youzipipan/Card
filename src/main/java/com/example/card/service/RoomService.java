package com.example.card.service;

import com.example.card.entities.Room;

import java.util.List;

public interface RoomService {
    List<Room> findByStudentId(String studentId, String s);
}
