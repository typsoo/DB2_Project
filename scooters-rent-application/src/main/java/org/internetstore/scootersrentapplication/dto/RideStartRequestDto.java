package org.internetstore.scootersrentapplication.dto;

public record RideStartRequestDto(
        Integer userId,
        Integer scooterId
) {}
