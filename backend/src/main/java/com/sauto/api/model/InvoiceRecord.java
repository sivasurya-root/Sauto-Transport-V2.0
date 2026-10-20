package com.sauto.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity @Table(name = "invoices")
public class InvoiceRecord extends OwnedRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String reference;
    public String customer;
    public String description;
    public Long amount;
    public LocalDate issuedOn;
    public LocalDate dueOn;
    public String status;
}
