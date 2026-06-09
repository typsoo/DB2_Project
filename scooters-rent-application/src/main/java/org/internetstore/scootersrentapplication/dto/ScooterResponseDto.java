package org.internetstore.scootersrentapplication.dto;

public record ScooterResponseDto(
        Integer id,
        String serialNumber,
        Integer chargeLevel,
        String status,

        Double latitude,
        Double longitude
) {}