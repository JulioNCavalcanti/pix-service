package com.julio.pixservice.infrastructure.persistence.repository;

import com.julio.pixservice.infrastructure.persistence.entity.PixKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPixKeyRepository extends JpaRepository<PixKeyEntity, UUID> {
    boolean existsByKey(String key);

    Optional<PixKeyEntity> findByKey(String key);
}
