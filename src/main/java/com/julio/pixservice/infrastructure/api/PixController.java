package com.julio.pixservice.infrastructure.api;

import com.julio.pixservice.application.dto.PixWebhookRequest;
import com.julio.pixservice.application.dto.TransferRequest;
import com.julio.pixservice.application.dto.TransferResponse;
import com.julio.pixservice.application.usecase.ProcessPixWebhookUseCase;
import com.julio.pixservice.application.usecase.TransferPixUseCase;
import com.julio.pixservice.infrastructure.simulator.PixNetworkSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {

    private final TransferPixUseCase transferPixUseCase;
    private final ProcessPixWebhookUseCase processPixWebhookUseCase;
    private final PixNetworkSimulator pixNetworkSimulator;

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> transfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody TransferRequest request) {

        TransferResponse response = transferPixUseCase.execute(request, idempotencyKey);
        
        pixNetworkSimulator.simulateNetworkResponse(response.endToEndId());

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody PixWebhookRequest request) {
        processPixWebhookUseCase.execute(request);
        return ResponseEntity.ok().build();
    }
}