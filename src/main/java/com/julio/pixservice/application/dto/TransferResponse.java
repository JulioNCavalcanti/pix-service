package com.julio.pixservice.application.dto;

import java.time.LocalDateTime;

public record TransferResponse(
        String endToEndId,
        String status,
        LocalDateTime createdAt
) {
}
