package com.example.card.repository;

import com.example.card.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student,String> {

    Student findByOpenId(String openId);

    Student findByCardNumberAndPassWord(String cardNumber, String password);

    @Modifying
    @Query("update Student s set s.openId=?3 where s.cardNumber=?1 and s.passWord=?2")
    int updateByCardNumberAndPassWord(String cardNumber, String passWord, String openId);

    Student findByStudentId(String studentId);

    Student findByCardNumber(String cardNumber);

    @Modifying
    @Query("update Student s set s.openId=?1 where s.studentId=?2")
    void updateByStudentId(String openId, String studentId);

    @Modifying
    void deleteByCardNumber(String cardNumber);
}
