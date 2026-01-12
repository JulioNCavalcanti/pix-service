package com.julio.pixservice.application.usecase;

import com.julio.pixservice.application.dto.TransferRequest;
import com.julio.pixservice.application.dto.TransferResponse;
import com.julio.pixservice.domain.gateway.LedgerGateway;
import com.julio.pixservice.domain.gateway.PixKeyGateway;
import com.julio.pixservice.domain.gateway.WalletGateway;
import com.julio.pixservice.domain.model.PixKey;
import com.julio.pixservice.domain.model.PixKeyType;
import com.julio.pixservice.domain.model.Wallet;
import com.julio.pixservice.infrastructure.persistence.entity.IdempotencyKeyEntity;
import com.julio.pixservice.infrastructure.persistence.entity.PixTransferEntity;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataIdempotencyKeyRepository;
import com.julio.pixservice.infrastructure.persistence.repository.SpringDataPixTransferRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferPixUseCaseTest {

    @InjectMocks
    private TransferPixUseCase useCase;

    @Mock
    private WalletGateway walletGateway;
    @Mock
    private PixKeyGateway pixKeyGateway;
    @Mock
    private LedgerGateway ledgerGateway;
    @Mock
    private SpringDataPixTransferRepository transferRepository;
    @Mock
    private SpringDataIdempotencyKeyRepository idempotencyRepository;

    @Test
    @DisplayName("Should execute transfer successfully when data is valid")
    void shouldExecuteTransferSuccessfully() {
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        String pixKeyStr = "user@test.com";
        BigDecimal amount = BigDecimal.TEN;

        Wallet fromWallet = new Wallet(fromWalletId, new BigDecimal("100.00"), LocalDateTime.now(), 1L);
        PixKey destPixKey = new PixKey(toWalletId, pixKeyStr, PixKeyType.EMAIL);

        when(walletGateway.findById(fromWalletId)).thenReturn(Optional.of(fromWallet));
        when(pixKeyGateway.findByKey(pixKeyStr)).thenReturn(Optional.of(destPixKey));

        TransferRequest request = new TransferRequest(fromWalletId, pixKeyStr, amount);

        TransferResponse response = useCase.execute(request, "idem-key-123");

        assertNotNull(response.endToEndId());
        assertEquals("PENDING", response.status());

        assertEquals(new BigDecimal("90.00"), fromWallet.getBalance());

        verify(walletGateway).save(fromWallet);
        verify(transferRepository).save(any(PixTransferEntity.class));
        verify(ledgerGateway).save(any());
        verify(idempotencyRepository).save(any());
    }

    @Test
    @DisplayName("Should return cached response if Idempotency Key exists")
    void shouldReturnCachedResponse_WhenIdempotencyKeyExists() {
        String idempotencyKey = "idem-key-duplicate";
        String existingE2eId = "E12345";
        String cachedJson = "{\"endToEndId\":\"" + existingE2eId + "\"}";

        when(idempotencyRepository.findById(idempotencyKey))
                .thenReturn(Optional.of(new IdempotencyKeyEntity(idempotencyKey, cachedJson, LocalDateTime.now())));

        TransferResponse response = useCase.execute(new TransferRequest(null, null, null), idempotencyKey);

        assertEquals(cachedJson, response.endToEndId());
        assertEquals("PENDING_REPLAY", response.status());

        verifyNoInteractions(walletGateway);
        verifyNoInteractions(transferRepository);
    }

    @Test
    @DisplayName("Should fail when trying to transfer to self")
    void shouldFail_WhenTransferToSelf() {
        UUID sameId = UUID.randomUUID();
        Wallet wallet = new Wallet(sameId, BigDecimal.TEN, LocalDateTime.now(), 1L);
        PixKey key = new PixKey(sameId, "mykey", PixKeyType.EVP);

        when(walletGateway.findById(sameId)).thenReturn(Optional.of(wallet));
        when(pixKeyGateway.findByKey("mykey")).thenReturn(Optional.of(key));

        TransferRequest request = new TransferRequest(sameId, "mykey", BigDecimal.ONE);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(request, null));
    }
}