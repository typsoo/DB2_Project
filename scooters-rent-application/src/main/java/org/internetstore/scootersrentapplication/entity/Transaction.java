package org.internetstore.scootersrentapplication.entity;

import jakarta.persistence.*;
import org.internetstore.scootersrentapplication.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // connection with wallets table
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    // connection with rides table
    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
