package com.julio.pixservice.domain.gateway;

import com.julio.pixservice.domain.model.LedgerEntry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface LedgerGateway {
    void save(LedgerEntry entry);

    BigDecimal getBalanceAt(UUID walletId, LocalDateTime date);
}
