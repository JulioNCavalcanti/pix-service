package com.julio.pixservice.application.dto;

import java.time.LocalDateTime;

public record PixWebhookRequest(
        String endToEndId,
        String eventId,
        String eventType,
        LocalDateTime occurredAt
) {
}
