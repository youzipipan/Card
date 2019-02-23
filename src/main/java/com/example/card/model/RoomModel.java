package com.example.card.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomModel {

    private String name;
    private String phone;
    private String room;
    private String returnTime;
    private String flag;
}
