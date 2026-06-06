package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.NotNull;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;

import java.time.LocalDateTime;

public final class ReservationDto {

    public record CreateRequest(
            @NotNull(message = "Scooter ID is mandatory")
            Integer scooterId
    ) {}

    public record Response(
            Integer id,
            Integer scooterId,
            ReservationStatus status,
            LocalDateTime expiresAt
    ) {}
}