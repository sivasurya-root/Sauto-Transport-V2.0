package com.sauto.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public final class Requests {
    private Requests() {}

    public record VehicleRequest(
        @NotBlank String name, @NotBlank String plate, @NotBlank String category,
        @NotBlank String status, @NotBlank String driver, @NotNull LocalDate nextService,
        @PositiveOrZero Integer mileage, @Min(0) @Max(100) Integer utilization,
        @Size(max = 2_000_000) String imageData
    ) {}

    public record DriverRequest(
        @NotBlank String name, @NotBlank String phone, @NotBlank @Email String email,
        @NotNull LocalDate licenseExpiry, String experience, @Min(0) @Max(5) Double rating, Boolean active
    ) {}

    public record DocumentRequest(
        @NotBlank String type, @NotBlank String entity, @NotBlank String title,
        String reference, @NotNull LocalDate expiresOn, String status
    ) {}

    public record PaymentRequest(
        @NotBlank String reference, @NotBlank String partner, @NotBlank String description,
        @NotNull @PositiveOrZero Long amount, @NotNull LocalDate dueOn, LocalDate paidOn,
        String status, String method
    ) {}

    public record InvoiceRequest(
        @NotBlank String reference, @NotBlank String customer, @NotBlank String description,
        @NotNull @PositiveOrZero Long amount, @NotNull LocalDate issuedOn, @NotNull LocalDate dueOn,
        String status
    ) {}

    public record StatusRequest(@NotBlank String status) {}
}
