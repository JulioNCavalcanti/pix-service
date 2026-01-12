package com.julio.pixservice.infrastructure.persistence.repository;

import com.julio.pixservice.infrastructure.persistence.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataIdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {
}
