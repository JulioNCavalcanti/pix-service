CREATE TABLE pix_keys (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    key VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_pix_keys_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

CREATE INDEX idx_pix_keys_key ON pix_keys(key);