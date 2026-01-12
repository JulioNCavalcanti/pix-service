CREATE TABLE pix_transfers (
    id UUID PRIMARY KEY,
    end_to_end_id VARCHAR(100) NOT NULL UNIQUE,
    from_wallet_id UUID NOT NULL,
    to_wallet_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_transfers_e2e ON pix_transfers(end_to_end_id);

CREATE TABLE idempotency_keys (
    key_id VARCHAR(100) PRIMARY KEY,
    response_json TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE processed_events (
    event_id VARCHAR(100) PRIMARY KEY,
    processed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);