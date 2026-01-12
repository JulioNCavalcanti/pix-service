package com.julio.pixservice.application.dto;

import com.julio.pixservice.domain.model.PixKey;
import com.julio.pixservice.domain.model.PixKeyType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PixKeyDTO(
        UUID id,
        String key,
        PixKeyType type,
        LocalDateTime createdAt
) {
    public static PixKeyDTO from(PixKey pixKey) {
        return new PixKeyDTO(
                pixKey.getId(),
                pixKey.getKey(),
                pixKey.getType(),
                pixKey.getCreatedAt()
        );
    }
}
