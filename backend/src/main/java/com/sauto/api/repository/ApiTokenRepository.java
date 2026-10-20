package com.sauto.api.repository;

import com.sauto.api.model.ApiToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiTokenRepository extends JpaRepository<ApiToken, String> {
    Optional<ApiToken> findByTokenHashAndExpiresAtAfter(String tokenHash, Instant now);
    void deleteAllByUserEmail(String userEmail);
}
