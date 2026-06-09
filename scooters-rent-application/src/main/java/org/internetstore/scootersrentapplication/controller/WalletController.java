package org.internetstore.scootersrentapplication.controller;

import org.internetstore.scootersrentapplication.dto.DepositDto;
import org.internetstore.scootersrentapplication.dto.WalletBalanceDto;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.internetstore.scootersrentapplication.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.internetstore.scootersrentapplication.entity.User;

import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletBalanceDto> deposit(
            @org.springframework.security.core.annotation.AuthenticationPrincipal User user,
            @Valid @RequestBody DepositDto dto) {

        Wallet updatedWallet = walletService.deposit(user.getId(), dto);
        WalletBalanceDto responseBody = new WalletBalanceDto(
                "Deposit successfully added",
                updatedWallet.getBalance()
        );

        return ResponseEntity.ok(responseBody);
    }
}