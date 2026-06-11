package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.DepositDto;
import org.internetstore.scootersrentapplication.dto.TransactionDto;
import org.internetstore.scootersrentapplication.entity.Transaction;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.internetstore.scootersrentapplication.entity.enums.TransactionType;
import org.internetstore.scootersrentapplication.repository.TransactionRepository;
import org.internetstore.scootersrentapplication.repository.WalletRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Wallet deposit(Integer userId, DepositDto dto) {
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found for this user"));


        BigDecimal newBalance = wallet.getBalance().add(dto.amount());
        wallet.setBalance(newBalance);

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(dto.amount());
        transaction.setType(TransactionType.DEPOSIT);

        transactionRepository.save(transaction);

        return wallet;
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionHistory(Integer userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return transactionRepository.findAll().stream()
                .filter(tx -> tx.getWallet().getId().equals(wallet.getId()))
                .map(tx -> new TransactionDto(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getRide() != null ? tx.getRide().getId() : null,
                        tx.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}