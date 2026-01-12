package com.julio.pixservice.infrastructure.persistence.repository;

import com.julio.pixservice.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {
}