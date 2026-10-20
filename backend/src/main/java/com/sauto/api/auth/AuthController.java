package com.sauto.api.auth;

import com.sauto.api.model.AppUser;
import com.sauto.api.repository.AppUserRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final AppUserRepository users;

    public AuthController(AuthService auth, AppUserRepository users) {
        this.auth = auth;
        this.users = users;
    }

    @PostMapping("/register")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRequest request) {
        return auth.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return auth.login(request);
    }

    @GetMapping("/me")
    public AuthResponse.UserSummary me(Principal principal) {
        AppUser user = users.findByEmail(principal.getName()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account was not found."));
        return new AuthResponse.UserSummary(user.email, user.name);
    }

    @PostMapping("/logout")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(Principal principal, @RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.logout(principal.getName(), authorization);
    }
}
