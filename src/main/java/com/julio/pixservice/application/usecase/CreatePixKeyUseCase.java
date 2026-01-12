package com.julio.pixservice.application.usecase;

import com.julio.pixservice.domain.gateway.PixKeyGateway;
import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.PixKey;
import com.julio.pixservice.domain.model.PixKeyType;
import com.julio.pixservice.domain.validation.PixKeyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePixKeyUseCase {

    private final PixKeyGateway pixKeyGateway;
    private final WalletGateway walletGateway;

    @Transactional
    public PixKey execute(UUID walletId, String key, PixKeyType type) {
        PixKeyValidator.validate(key, type);

        if (walletGateway.findById(walletId).isEmpty()) {
            throw new IllegalArgumentException("Wallet not found");
        }

        if (pixKeyGateway.existsByKey(key)) {
            throw new IllegalArgumentException("Pix Key already exists");
        }

        PixKey newPixKey = new PixKey(walletId, key, type);
        return pixKeyGateway.save(newPixKey);
    }
}
