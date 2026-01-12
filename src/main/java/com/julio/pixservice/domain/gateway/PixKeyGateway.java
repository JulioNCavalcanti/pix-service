package com.julio.pixservice.domain.gateway;

import com.julio.pixservice.domain.model.PixKey;

import java.util.Optional;

public interface PixKeyGateway {
    PixKey save(PixKey pixKey);

    boolean existsByKey(String key);

    Optional<PixKey> findByKey(String key);
}
