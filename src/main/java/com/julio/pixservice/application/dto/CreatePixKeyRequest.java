package com.julio.pixservice.application.dto;

import com.julio.pixservice.domain.model.PixKeyType;

public record CreatePixKeyRequest(
        String key,
        PixKeyType type
) {
}
