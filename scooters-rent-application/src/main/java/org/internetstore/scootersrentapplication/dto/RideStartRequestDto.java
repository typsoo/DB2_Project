package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.NotNull;

public record RideStartRequestDto(
        @NotNull(message = "Scooter ID is mandatory")
        Integer scooterId
) {}
