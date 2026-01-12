package com.julio.pixservice.application.usecase;

import com.julio.pixservice.application.dto.PixWebhookRequest;
import com.julio.pixservice.domain.gateway.LedgerGateway;
import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.LedgerEntry;
import com.julio.pixservice.domain.model.TransactionType;
import com.julio.pixservice.domain.model.Wallet;
import com.julio.pixservice.infrastructure.persistence.entity.PixTransferEntity;
import com.julio.pixservice.infrastructure.persistence.entity.ProcessedEventEntity;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataPixTransferRepository;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPixWebhookUseCase {
    private final SpringDataPixTransferRepository transferRepository;
    private final SpringDataProcessedEventRepository eventRepository;
    private final WalletGateway walletGateway;
    private final LedgerGateway ledgerGateway;

    @Transactional
    public void execute(PixWebhookRequest request) {
        if (eventRepository.existsById(request.eventId())) {
            log.info("Event {} already processed. Ignoring.", request.eventId());
            return;
        }

        PixTransferEntity transfer = transferRepository.findByEndToEndId(request.endToEndId())
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found: " + request.endToEndId()));

        if (!"PENDING".equals(transfer.getStatus())) {
            log.warn("Transfer {} is already {}. Ignoring update.", transfer.getEndToEndId(), transfer.getStatus());
            saveEvent(request.eventId());
            return;
        }

        if ("CONFIRMED".equals(request.eventType())) {
            confirmTransfer(transfer);
        } else if ("REJECTED".equals(request.eventType())) {
            rejectTransfer(transfer);
        }

        saveEvent(request.eventId());
    }

    private void confirmTransfer(PixTransferEntity transfer) {
        Wallet toWallet = walletGateway.findById(transfer.getToWalletId())
                .orElseThrow(() -> new IllegalStateException("Destination Wallet not found!"));

        toWallet.deposit(transfer.getAmount());
        walletGateway.save(toWallet);

        transfer.setStatus("CONFIRMED");
        transfer.setUpdatedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        ledgerGateway.save(new LedgerEntry(
                toWallet.getId(), transfer.getAmount(), TransactionType.PIX_IN, transfer.getEndToEndId()
        ));

        log.info("Transfer {} CONFIRMED. Credit applied.", transfer.getEndToEndId());
    }

    private void rejectTransfer(PixTransferEntity transfer) {
        Wallet fromWallet = walletGateway.findById(transfer.getFromWalletId())
                .orElseThrow(() -> new IllegalStateException("Source Wallet not found!"));

        fromWallet.deposit(transfer.getAmount());
        walletGateway.save(fromWallet);

        transfer.setStatus("REJECTED");
        transfer.setUpdatedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        ledgerGateway.save(new LedgerEntry(
                fromWallet.getId(), transfer.getAmount(), TransactionType.DEPOSIT, transfer.getEndToEndId() + "-REFUND"
        ));

        log.info("Transfer {} REJECTED. Refund processed.", transfer.getEndToEndId());
    }

    private void saveEvent(String eventId) {
        eventRepository.save(new ProcessedEventEntity(eventId, LocalDateTime.now()));
    }
}