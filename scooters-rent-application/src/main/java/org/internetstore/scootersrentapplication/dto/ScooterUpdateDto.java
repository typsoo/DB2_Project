package org.internetstore.scootersrentapplication.dto;

import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;

public record ScooterUpdateDto(
        Integer chargeLevel,
        ScooterStatus status
) {}
