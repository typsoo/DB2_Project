package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RideEndRequestDto(
        @NotNull(message = "Ride ID is mandatory")
        Integer rideId,
        
        @NotNull(message = "Distance is mandatory")
        @Positive(message = "Distance must be positive")
        Double distance
) {}
