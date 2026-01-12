package com.julio.pixservice.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        UUID fromWalletId,
        String toPixKey,
        BigDecimal amount
) {
}
