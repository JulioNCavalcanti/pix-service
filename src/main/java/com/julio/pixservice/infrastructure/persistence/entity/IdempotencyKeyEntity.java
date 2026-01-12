package com.julio.pixservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyKeyEntity implements Persistable<String> {

    @Id
    @Column(name = "key_id")
    private String keyId;

    @Column(name = "response_json", columnDefinition = "TEXT")
    private String responseJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Override
    public String getId() {
        return keyId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
