package com.sauto.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false, unique = true, length = 190)
    public String email;
    @Column(nullable = false, length = 100)
    public String name;
    @Column(nullable = false, length = 100)
    protected String passwordHash;

    protected AppUser() {}
    public AppUser(String email, String name, String passwordHash) {
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public String passwordHash() { return passwordHash; }
}
