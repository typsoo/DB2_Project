package org.internetstore.scootersrentapplication.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideEndResponseDto(
        Integer rideId,
        BigDecimal totalCost,
        LocalDateTime endTime,
        Long durationInMinutes
) {}
