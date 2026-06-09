package org.internetstore.scootersrentapplication.dto;

public record ScooterCreateDto(
        String serialNumber,
        Double latitude,
        Double longitude
) {}