package com.julio.pixservice.infrastructure.persistence;

import com.julio.pixservice.domain.gateway.LedgerGateway;
import com.julio.pixservice.domain.model.LedgerEntry;
import com.julio.pixservice.infrastructure.persistence.entity.LedgerEntity;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LedgerGatewayImpl implements LedgerGateway {

    private final SpringDataLedgerRepository repository;

    @Override
    public void save(LedgerEntry entry) {
        LedgerEntity entity = new LedgerEntity(
                entry.getId(),
                entry.getWalletId(),
                entry.getAmount(),
                entry.getType().name(),
                entry.getEndToEndId(),
                entry.getCreatedAt()
        );
        repository.save(entity);
    }

    @Override
    public BigDecimal getBalanceAt(UUID walletId, LocalDateTime date) {
        // Chama a query personalizada que criamos no passo anterior
        return repository.calculateBalanceUntil(walletId, date);
    }
}
