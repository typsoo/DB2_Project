package org.internetstore.scootersrentapplication.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserProfileDto(
        Integer id,
        String email,
        String firstName,
        String lastName,
        BigDecimal balance,
        LocalDateTime createdAt
) {}