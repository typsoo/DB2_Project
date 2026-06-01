package org.internetstore.scootersrentapplication.dto;

import java.math.BigDecimal;

public class WalletBalanceDto {
    private String message;
    private BigDecimal balance;

//    public WalletBalanceDto() {
//    }

    public WalletBalanceDto(String message, BigDecimal balance) {
        this.message = message;
        this.balance = balance;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}