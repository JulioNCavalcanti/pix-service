package com.julio.pixservice.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BalanceDTO(
        UUID walletId,
        BigDecimal balance,
        LocalDateTime referenceDate
) {
}
