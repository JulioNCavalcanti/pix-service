package com.julio.pixservice.domain.gateway;

import com.julio.pixservice.domain.model.LedgerEntry;

public interface LedgerGateway {
    void save(LedgerEntry entry);
}
