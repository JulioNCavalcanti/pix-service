package com.julio.pixservice.infrastructure.persistence.entity;

import com.julio.pixservice.domain.model.PixKeyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pix_keys")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixKeyEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PixKeyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean isNew() {
        return true;
    }
}
