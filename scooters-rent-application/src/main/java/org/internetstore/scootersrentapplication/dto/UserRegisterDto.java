package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String email,
        
        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
        
        @NotBlank(message = "First name is mandatory")
        String firstName,
        
        @NotBlank(message = "Last name is mandatory")
        String lastName
) {}