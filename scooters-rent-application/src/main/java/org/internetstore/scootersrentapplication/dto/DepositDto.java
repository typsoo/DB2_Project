package org.internetstore.scootersrentapplication.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DepositDto(
        @NotNull(message = "Amount is mandatory")
        @DecimalMin(value = "0.01", message = "Deposit amount must be strictly positive")
        BigDecimal amount
) {}