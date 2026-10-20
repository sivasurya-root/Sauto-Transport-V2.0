package com.sauto.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Lob;
import java.time.LocalDate;

@Entity @Table(name = "vehicles")
public class Vehicle extends OwnedRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String plate;
    public String category;
    public String status;
    public String driver;
    public LocalDate nextService;
    public Integer mileage;
    public Integer utilization;
    public String tone;
    @Lob
    public String imageData;
}
