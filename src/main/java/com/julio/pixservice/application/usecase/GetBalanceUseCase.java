package com.julio.pixservice.application.usecase;

import com.julio.pixservice.domain.gateway.LedgerGateway;
import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBalanceUseCase {

    private final WalletGateway walletGateway;
    private final LedgerGateway ledgerGateway;

    @Transactional(readOnly = true)
    public BigDecimal execute(UUID walletId, LocalDateTime at) {
        if (walletGateway.findById(walletId).isEmpty()) {
            throw new IllegalArgumentException("Wallet not found");
        }

        if (at != null) {
            return ledgerGateway.getBalanceAt(walletId, at);
        }

        Wallet wallet = walletGateway.findById(walletId).get();
        return wallet.getBalance();
    }
}
