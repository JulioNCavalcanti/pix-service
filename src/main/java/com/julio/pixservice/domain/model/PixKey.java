package com.julio.pixservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class PixKey {
    private final UUID id;
    private final UUID walletId;
    private final String key;
    private final PixKeyType type;
    private final LocalDateTime createdAt;

    public PixKey(UUID walletId, String key, PixKeyType type) {
        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.key = key;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public PixKey(UUID id, UUID walletId, String key, PixKeyType type, LocalDateTime createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.key = key;
        this.type = type;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public String getKey() {
        return key;
    }

    public PixKeyType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
