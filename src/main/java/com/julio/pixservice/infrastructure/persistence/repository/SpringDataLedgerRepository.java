package com.julio.pixservice.infrastructure.persistence.repository;

import com.julio.pixservice.infrastructure.persistence.entity.LedgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface SpringDataLedgerRepository extends JpaRepository<LedgerEntity, UUID> {
    @Query("""
                SELECT COALESCE(SUM(
                    CASE 
                        WHEN l.type IN ('DEPOSIT', 'PIX_IN') THEN l.amount 
                        WHEN l.type IN ('WITHDRAW', 'PIX_OUT') THEN -l.amount 
                        ELSE 0 
                    END
                ), 0)
                FROM LedgerEntity l 
                WHERE l.walletId = :walletId 
                AND l.createdAt <= :date
            """)
    BigDecimal calculateBalanceUntil(@Param("walletId") UUID walletId, @Param("date") LocalDateTime date);
}
