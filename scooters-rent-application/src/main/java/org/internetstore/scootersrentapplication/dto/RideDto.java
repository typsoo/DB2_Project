package org.internetstore.scootersrentapplication.dto;

import java.time.LocalDateTime;

public record RideDto(
        Integer rideId,
        Integer scooterId,
        LocalDateTime startTime
) {}
