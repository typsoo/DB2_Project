package org.internetstore.scootersrentapplication.dto;

import org.internetstore.scootersrentapplication.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        Integer id,
        BigDecimal amount,
        TransactionType type,
        Integer rideId,
        LocalDateTime createdAt
) {}
