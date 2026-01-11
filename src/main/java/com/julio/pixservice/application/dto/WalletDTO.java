package com.julio.pixservice.application.dto;

import com.julio.pixservice.domain.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletDTO(
        UUID id,
        BigDecimal balance,
        LocalDateTime createdAt
) {
    public static WalletDTO from(Wallet wallet) {
        return new WalletDTO(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCreatedAt()
        );
    }
}
