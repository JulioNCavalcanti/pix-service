package com.julio.pixservice.domain.gateway;

import com.julio.pixservice.domain.model.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletGateway {
    Wallet save(Wallet wallet);

    Optional<Wallet> findById(UUID id);
}
