package com.julio.pixservice.infrastructure.persistence;

import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.Wallet;
import com.julio.pixservice.infrastructure.persistence.entity.WalletEntity;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletGatewayImpl implements WalletGateway {
    private final SpringDataWalletRepository repository;

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = new WalletEntity(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCreatedAt(),
                wallet.getVersion()
        );

        WalletEntity savedEntity = repository.save(entity);

        return new Wallet(
                savedEntity.getId(),
                savedEntity.getBalance(),
                savedEntity.getCreatedAt(),
                savedEntity.getVersion()
        );
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return repository.findById(id)
                .map(entity -> new Wallet(
                        entity.getId(),
                        entity.getBalance(),
                        entity.getCreatedAt(),
                        entity.getVersion()
                ));
    }
}
