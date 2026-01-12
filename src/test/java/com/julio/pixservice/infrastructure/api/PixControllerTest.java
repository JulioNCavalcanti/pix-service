package com.julio.pixservice.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.julio.pixservice.application.dto.TransferRequest;
import com.julio.pixservice.application.dto.TransferResponse;
import com.julio.pixservice.application.usecase.ProcessPixWebhookUseCase;
import com.julio.pixservice.application.usecase.TransferPixUseCase;
import com.julio.pixservice.infrastructure.simulator.PixNetworkSimulator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType; // <--- FALTAVA ESTE IMPORT
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PixController.class)
class PixControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferPixUseCase transferPixUseCase;
    @MockitoBean
    private ProcessPixWebhookUseCase processPixWebhookUseCase;
    @MockitoBean
    private PixNetworkSimulator pixNetworkSimulator;

    @Test
    @DisplayName("POST /pix/transfers - Should return 202 Accepted")
    void shouldReturnAccepted_WhenTransferInitiated() throws Exception {
        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                "chave@pix.com",
                new BigDecimal("50.00")
        );

        TransferResponse mockResponse = new TransferResponse(
                "E123456789",
                "PENDING",
                LocalDateTime.now()
        );

        when(transferPixUseCase.execute(any(TransferRequest.class), eq("idempotency-123")))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/pix/transfers")
                        .header("Idempotency-Key", "idempotency-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.endToEndId").value("E123456789"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}