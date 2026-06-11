package org.internetstore.scootersrentapplication.dto;

import java.math.BigDecimal;

public record WalletBalanceDto(
        String message,
        BigDecimal balance
) {}