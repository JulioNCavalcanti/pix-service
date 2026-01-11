package com.julio.pixservice.infrastructure.api;

import com.julio.pixservice.application.dto.WalletDTO;
import com.julio.pixservice.application.usecase.CreateWalletUseCase;
import com.julio.pixservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final CreateWalletUseCase createWalletUseCase;

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet() {
        Wallet createdWallet = createWalletUseCase.execute();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WalletDTO.from(createdWallet));
    }
}
