package com.sauto.api.auth;

import java.time.Instant;

public record AuthResponse(String token, Instant expiresAt, UserSummary user) {
    public record UserSummary(String email, String name) {}
}
