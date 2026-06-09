package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScooterCreateDto(
        @NotBlank(message = "Serial number is mandatory")
        String serialNumber,
        
        @NotNull(message = "Latitude is mandatory")
        Double latitude,
        
        @NotNull(message = "Longitude is mandatory")
        Double longitude
) {}