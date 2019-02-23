package com.example.card.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="t_room")
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    private String UUID;
    @Column
    private String studentId;// 学生ID
    @Column
    private String room;//宿舍
    @Column
    private String returnTime;//回校时间
    @Column
    private String flag;//0 未回校   1 未按时回校   2 已回校
}
