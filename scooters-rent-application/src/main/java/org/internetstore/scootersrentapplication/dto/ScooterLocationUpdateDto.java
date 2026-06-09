package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.NotNull;

public record ScooterLocationUpdateDto(
        @NotNull(message = "Latitude is mandatory")
        Double latitude,
        
        @NotNull(message = "Longitude is mandatory")
        Double longitude
) {}