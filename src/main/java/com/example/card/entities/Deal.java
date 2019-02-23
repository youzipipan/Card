package com.example.card.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="t_deal")
@AllArgsConstructor
@NoArgsConstructor
public class Deal {
}
