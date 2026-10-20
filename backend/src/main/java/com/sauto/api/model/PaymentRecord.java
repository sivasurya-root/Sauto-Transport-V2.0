package com.sauto.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity @Table(name = "payments")
public class PaymentRecord extends OwnedRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String reference;
    public String partner;
    public String description;
    public Long amount;
    public LocalDate dueOn;
    public LocalDate paidOn;
    public String status;
    @Column(name = "payment_method")
    public String method;
}
