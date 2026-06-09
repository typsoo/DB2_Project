package org.internetstore.scootersrentapplication.dto;

public record UserRegisterDto(
        String email,
        String password,
        String firstName,
        String lastName
) {}