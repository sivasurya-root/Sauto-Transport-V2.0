package com.sauto.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "api_tokens")
public class ApiToken {
    @Id
    @Column(length = 64)
    public String tokenHash;
    @Column(nullable = false, length = 190)
    public String userEmail;
    @Column(nullable = false)
    public Instant expiresAt;

    protected ApiToken() {}
    public ApiToken(String tokenHash, String userEmail, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.userEmail = userEmail;
        this.expiresAt = expiresAt;
    }
}
