package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.NotNull;

public record RideStartRequestDto(
        @NotNull(message = "User ID is mandatory")
        Integer userId,
        
        @NotNull(message = "Scooter ID is mandatory")
        Integer scooterId
) {}
