package org.internetstore.scootersrentapplication.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideHistoryResponseDto(
        Integer id,
        Integer userId,
        Integer scooterId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Double distance,
        BigDecimal totalCost
) {}
