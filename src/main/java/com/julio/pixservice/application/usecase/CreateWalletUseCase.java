package com.julio.pixservice.application.usecase;

import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateWalletUseCase {
    private final WalletGateway walletGateway;

    public Wallet execute() {
        Wallet newWallet = new Wallet();

        return walletGateway.save(newWallet);
    }
}
