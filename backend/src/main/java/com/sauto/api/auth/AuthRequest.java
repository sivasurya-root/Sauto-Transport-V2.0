package com.sauto.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
    @NotBlank @Email @Size(max = 190) String email,
    @NotBlank @Size(min = 10, max = 72) String password,
    @Size(max = 100) String name
) {}
