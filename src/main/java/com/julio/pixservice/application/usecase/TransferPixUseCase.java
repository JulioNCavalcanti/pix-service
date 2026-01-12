package com.julio.pixservice.application.usecase;

import com.julio.pixservice.application.dto.TransferRequest;
import com.julio.pixservice.application.dto.TransferResponse;
import com.julio.pixservice.domain.gateway.LedgerGateway;
import com.julio.pixservice.domain.gateway.PixKeyGateway;
import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.LedgerEntry;
import com.julio.pixservice.domain.model.PixKey;
import com.julio.pixservice.domain.model.TransactionType;
import com.julio.pixservice.domain.model.Wallet;
import com.julio.pixservice.infrastructure.persistence.entity.IdempotencyKeyEntity;
import com.julio.pixservice.infrastructure.persistence.entity.PixTransferEntity;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataIdempotencyKeyRepository;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataPixTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferPixUseCase {
    private final WalletGateway walletGateway;
    private final PixKeyGateway pixKeyGateway;
    private final LedgerGateway ledgerGateway;
    private final SpringDataPixTransferRepository transferRepository;
    private final SpringDataIdempotencyKeyRepository idempotencyRepository;

    @Transactional
    public TransferResponse execute(TransferRequest request, String idempotencyKey) {
        if (idempotencyKey != null) {
            Optional<IdempotencyKeyEntity> cached = idempotencyRepository.findById(idempotencyKey);
            if (cached.isPresent()) {
                log.info("Idempotency key hit: {}. Returning cached response.", idempotencyKey);
                return new TransferResponse(cached.get().getResponseJson(), "PENDING_REPLAY", LocalDateTime.now());
            }
        }

        log.info("Initiating Pix transfer from wallet ID: {}", request.fromWalletId());

        Wallet fromWallet = walletGateway.findById(request.fromWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found"));

        PixKey destKey = pixKeyGateway.findByKey(request.toPixKey())
                .orElseThrow(() -> new IllegalArgumentException("Destination Pix Key not found"));

        if (fromWallet.getId().equals(destKey.getWalletId())) {
            log.warn("Attempt to transfer to self. Wallet ID: {}", fromWallet.getId());
            throw new IllegalArgumentException("Cannot transfer to self");
        }

        // Debit execution
        fromWallet.withdraw(request.amount());
        walletGateway.save(fromWallet);

        String e2eId = "E" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PixTransferEntity transfer = new PixTransferEntity(
                UUID.randomUUID(), e2eId, fromWallet.getId(), destKey.getWalletId(),
                request.amount(), "PENDING", LocalDateTime.now(), null
        );
        transferRepository.save(transfer);

        ledgerGateway.save(new LedgerEntry(
                fromWallet.getId(), request.amount(), TransactionType.PIX_OUT, e2eId
        ));

        if (idempotencyKey != null) {
            idempotencyRepository.save(new IdempotencyKeyEntity(idempotencyKey, e2eId, LocalDateTime.now()));
        }

        log.info("Pix transfer registered successfully. EndToEndId: {}, Status: PENDING", e2eId);

        return new TransferResponse(e2eId, "PENDING", LocalDateTime.now());
    }
}