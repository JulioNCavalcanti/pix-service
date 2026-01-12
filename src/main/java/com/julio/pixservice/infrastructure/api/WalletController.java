package com.julio.pixservice.infrastructure.api;

import com.julio.pixservice.application.dto.WalletDTO;
import com.julio.pixservice.application.dto.WalletTransactionRequest;
import com.julio.pixservice.application.usecase.CreateWalletUseCase;
import com.julio.pixservice.application.usecase.DepositUseCase;
import com.julio.pixservice.application.usecase.WithdrawUseCase;
import com.julio.pixservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final CreateWalletUseCase createWalletUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet() {
        Wallet createdWallet = createWalletUseCase.execute();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WalletDTO.from(createdWallet));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<WalletDTO> deposit(@PathVariable UUID id, @RequestBody WalletTransactionRequest request) {
        Wallet updatedWallet = depositUseCase.execute(id, request.amount());
        return ResponseEntity.ok(WalletDTO.from(updatedWallet));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<WalletDTO> withdraw(@PathVariable UUID id, @RequestBody WalletTransactionRequest request) {
        Wallet updatedWallet = withdrawUseCase.execute(id, request.amount());
        return ResponseEntity.ok(WalletDTO.from(updatedWallet));
    }
}
