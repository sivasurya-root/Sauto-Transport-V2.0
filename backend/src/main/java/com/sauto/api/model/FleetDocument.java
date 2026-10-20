package com.sauto.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity @Table(name = "fleet_documents")
public class FleetDocument extends OwnedRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String type;
    @Column(name = "assigned_entity")
    public String entity;
    public String title;
    public String reference;
    public LocalDate expiresOn;
    public String status;
}
