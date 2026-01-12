package com.julio.pixservice.infrastructure.simulator;

import com.julio.pixservice.application.dto.PixWebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PixNetworkSimulator {

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void simulateNetworkResponse(String endToEndId) {
        try {
            log.info("Simulating Central Bank processing for e2eId: {}", endToEndId);
            Thread.sleep(5000); // simulate newtork delay

            String url = "http://localhost:8080/pix/webhook";

            var request = new PixWebhookRequest(
                    endToEndId,
                    UUID.randomUUID().toString(),
                    "CONFIRMED",
                    LocalDateTime.now()
            );

            restTemplate.postForEntity(url, request, Void.class);
            log.info("Webhook successfully sent for e2eId: {}", endToEndId);

        } catch (Exception e) {
            log.error("Failed to simulate Pix network", e);
        }
    }
}