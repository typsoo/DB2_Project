package org.internetstore.scootersrentapplication.dto;

import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;
import java.time.LocalDateTime;

public record ReservationResponseDto(
        Integer id,
        Integer scooterId,
        ReservationStatus status,
        LocalDateTime expiresAt
) {}
