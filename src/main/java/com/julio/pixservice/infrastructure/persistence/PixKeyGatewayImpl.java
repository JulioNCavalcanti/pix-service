package com.julio.pixservice.infrastructure.persistence;

import com.julio.pixservice.domain.gateway.PixKeyGateway;
import com.julio.pixservice.domain.model.PixKey;
import com.julio.pixservice.infrastructure.persistence.entity.PixKeyEntity;
import com.julio.pixservice.infrastructure.persistence.entity.WalletEntity;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataPixKeyRepository;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PixKeyGatewayImpl implements PixKeyGateway {

    private final SpringDataPixKeyRepository pixKeyRepository;
    private final SpringDataWalletRepository walletRepository;

    @Override
    public PixKey save(PixKey pixKey) {
        WalletEntity walletRef = walletRepository.getReferenceById(pixKey.getWalletId());

        PixKeyEntity entity = new PixKeyEntity(
                pixKey.getId(),
                pixKey.getKey(),
                pixKey.getType(),
                walletRef,
                pixKey.getCreatedAt()
        );

        PixKeyEntity saved = pixKeyRepository.save(entity);

        return new PixKey(
                saved.getId(),
                saved.getWallet().getId(),
                saved.getKey(),
                saved.getType(),
                saved.getCreatedAt()
        );
    }

    @Override
    public boolean existsByKey(String key) {
        return pixKeyRepository.existsByKey(key);
    }

    @Override
    public Optional<PixKey> findByKey(String key) {
        return pixKeyRepository.findByKey(key)
                .map(entity -> new PixKey(
                        entity.getId(),
                        entity.getWallet().getId(),
                        entity.getKey(),
                        entity.getType(),
                        entity.getCreatedAt()
                ));
    }
}

