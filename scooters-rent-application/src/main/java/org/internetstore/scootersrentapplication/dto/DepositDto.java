package org.internetstore.scootersrentapplication.dto;

import java.math.BigDecimal;

public class DepositDto {
    private BigDecimal amount;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}