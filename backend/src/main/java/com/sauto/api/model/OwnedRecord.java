package com.sauto.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class OwnedRecord {
    @Column(nullable = false, length = 190)
    protected String ownerEmail;

    public void assignOwner(String email) { this.ownerEmail = email; }
}
