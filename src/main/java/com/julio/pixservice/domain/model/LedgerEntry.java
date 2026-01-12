package com.julio.pixservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class LedgerEntry {
    private final UUID id;
    private final UUID walletId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String endToEndId;
    private final LocalDateTime createdAt;

    public LedgerEntry(UUID walletId, BigDecimal amount, TransactionType type, String endToEndId) {
        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.endToEndId = endToEndId;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}