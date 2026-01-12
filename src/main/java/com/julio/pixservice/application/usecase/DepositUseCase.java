package com.julio.pixservice.application.usecase;

import com.julio.pixservice.domain.gateway.LedgerGateway;
import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.LedgerEntry;
import com.julio.pixservice.domain.model.TransactionType;
import com.julio.pixservice.domain.model.Wallet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepositUseCase {

    private final WalletGateway walletGateway;
    private final LedgerGateway ledgerGateway;

    @Transactional
    public Wallet execute(UUID walletId, BigDecimal amount) {
        Wallet wallet = walletGateway.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        wallet.deposit(amount);

        Wallet updatedWallet = walletGateway.save(wallet);

        String fakeEndToEndId = "D" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LedgerEntry entry = new LedgerEntry(
                updatedWallet.getId(),
                amount,
                TransactionType.DEPOSIT,
                fakeEndToEndId
        );

        ledgerGateway.save(entry);

        return updatedWallet;
    }
}
