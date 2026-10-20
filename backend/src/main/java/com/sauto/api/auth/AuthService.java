package com.sauto.api.auth;

import com.sauto.api.model.ApiToken;
import com.sauto.api.model.AppUser;
import com.sauto.api.repository.ApiTokenRepository;
import com.sauto.api.repository.AppUserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final AppUserRepository users;
    private final ApiTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final int tokenHours;

    public AuthService(AppUserRepository users, ApiTokenRepository tokens, PasswordEncoder passwordEncoder,
                       @Value("${sauto.token-hours}") int tokenHours) {
        this.users = users;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.tokenHours = Math.max(1, tokenHours);
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for this email.");
        }
        String name = request.name() == null || request.name().isBlank()
            ? email.substring(0, email.indexOf('@')) : request.name().trim();
        AppUser user = users.save(new AppUser(email, name, passwordEncoder.encode(request.password())));
        return issueToken(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        String email = request.email().trim().toLowerCase();
        AppUser user = users.findByEmail(email).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect."));
        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
        }
        return issueToken(user);
    }

    @Transactional
    public void logout(String email, String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String hash = hashToken(bearerToken.substring(7).trim());
            tokens.findById(hash).filter(token -> token.userEmail.equals(email)).ifPresent(tokens::delete);
        }
    }

    private AuthResponse issueToken(AppUser user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiresAt = Instant.now().plus(tokenHours, ChronoUnit.HOURS);
        tokens.save(new ApiToken(hashToken(token), user.email, expiresAt));
        return new AuthResponse(token, expiresAt, new AuthResponse.UserSummary(user.email, user.name));
    }

    public static String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
