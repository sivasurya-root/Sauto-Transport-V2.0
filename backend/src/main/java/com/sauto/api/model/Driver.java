package com.sauto.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity @Table(name = "drivers")
public class Driver extends OwnedRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String phone;
    public String email;
    public LocalDate licenseExpiry;
    public String experience;
    public Double rating;
    public Boolean active;
    public String tone;
}
