DROP TABLE IF EXISTS wallet_entries;

CREATE TABLE wallet_entries (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    wallet_account_id BIGINT UNSIGNED NOT NULL,
    entry_type VARCHAR(40) NOT NULL,
    amount_minor BIGINT NOT NULL,
    balance_after_minor BIGINT NOT NULL,
    reference_type VARCHAR(40) NULL,
    reference_id BIGINT UNSIGNED NULL,
    note VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_wallet_entries_wallet_account
        FOREIGN KEY (wallet_account_id) REFERENCES wallet_accounts (id) ON DELETE CASCADE,
    INDEX idx_wallet_entries_account_created (wallet_account_id, created_at),
    INDEX idx_wallet_entries_reference (reference_type, reference_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;