package org.internetstore.scootersrentapplication.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        Integer id,
        String email,
        String firstName,
        String lastName,
        String role,
        LocalDateTime createdAt
) {}
