package com.julio.pixservice.domain.model;

import com.julio.pixservice.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Wallet {
    private final UUID id;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private Long version;

    public Wallet() {
        this.id = UUID.randomUUID();
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.version = null;
    }

    public Wallet(UUID id, BigDecimal balance, LocalDateTime createdAt, Long version) {
        this.id = id;
        this.balance = balance;
        this.createdAt = createdAt;
        this.version = version;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for this operation");
        }
        this.balance = this.balance.subtract(amount);
    }

    public UUID getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getVersion() { return version; }
}
