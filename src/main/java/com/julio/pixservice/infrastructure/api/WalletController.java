package com.julio.pixservice.infrastructure.api;

import com.julio.pixservice.application.dto.*;
import com.julio.pixservice.application.usecase.*;
import com.julio.pixservice.domain.model.PixKey;
import com.julio.pixservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final CreateWalletUseCase createWalletUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final CreatePixKeyUseCase createPixKeyUseCase;

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

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceDTO> getBalance(
            @PathVariable UUID id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime at) {

        BigDecimal balance = getBalanceUseCase.execute(id, at);

        return ResponseEntity.ok(new BalanceDTO(
                id,
                balance,
                at != null ? at : LocalDateTime.now()
        ));
    }

    @PostMapping("/{id}/pix-keys")
    public ResponseEntity<PixKeyDTO> createPixKey(
            @PathVariable UUID id,
            @RequestBody CreatePixKeyRequest request) {

        PixKey createdKey = createPixKeyUseCase.execute(id, request.key(), request.type());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PixKeyDTO.from(createdKey));
    }
}
