package com.julio.pixservice.infrastructure.persistence.repository;

import com.julio.pixservice.infrastructure.persistence.entity.LedgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataLedgerRepository extends JpaRepository<LedgerEntity, UUID> {
}
