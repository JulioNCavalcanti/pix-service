package com.julio.pixservice.infrastructure.persistence.repository;

import com.julio.pixservice.infrastructure.persistence.entity.PixTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPixTransferRepository extends JpaRepository<PixTransferEntity, UUID> {
    Optional<PixTransferEntity> findByEndToEndId(String endToEndId);
}
