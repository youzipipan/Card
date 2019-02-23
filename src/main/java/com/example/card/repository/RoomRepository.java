package com.example.card.repository;

import com.example.card.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room,String> {
    List<Room> findByStudentIdAndFlag(String studentId, String s);
}
