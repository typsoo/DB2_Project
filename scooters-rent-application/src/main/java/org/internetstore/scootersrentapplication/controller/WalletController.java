package org.internetstore.scootersrentapplication.controller;

import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.DepositDto;
import org.internetstore.scootersrentapplication.dto.TransactionDto;
import org.internetstore.scootersrentapplication.dto.WalletBalanceDto;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.internetstore.scootersrentapplication.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.internetstore.scootersrentapplication.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // GET /api/wallets/me/transactions
    // Retrieves the transaction history (deposits and payments) for the authenticated user
    @GetMapping("/me/transactions")
    public ResponseEntity<List<TransactionDto>> getMyTransactions(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(walletService.getTransactionHistory(user.getId()));
    }

    // POST /api/wallets/deposit
    // Adds funds to the authenticated user's wallet
    @PostMapping("/deposit")
    public ResponseEntity<WalletBalanceDto> deposit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DepositDto dto
    ) {
        Wallet updatedWallet = walletService.deposit(user.getId(), dto);
        WalletBalanceDto responseBody = new WalletBalanceDto(
                "Deposit successfully added",
                updatedWallet.getBalance()
        );
        return ResponseEntity.ok(responseBody);
    }
}