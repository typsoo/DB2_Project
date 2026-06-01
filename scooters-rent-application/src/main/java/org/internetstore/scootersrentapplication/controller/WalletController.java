package org.internetstore.scootersrentapplication.controller;

import org.internetstore.scootersrentapplication.dto.DepositDto;
import org.internetstore.scootersrentapplication.dto.WalletBalanceDto;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.internetstore.scootersrentapplication.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{user_id}/deposit")
    public ResponseEntity<WalletBalanceDto> deposit(
            @PathVariable("user_id") Integer userId,
            @RequestBody DepositDto dto) {

        Wallet updatedWallet = walletService.deposit(userId, dto);
        WalletBalanceDto responseBody = new WalletBalanceDto(
                "Deposit successful",
                updatedWallet.getBalance()
        );

        return ResponseEntity.ok(responseBody);
    }
}