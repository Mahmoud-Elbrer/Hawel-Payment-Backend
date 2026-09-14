CREATE DATABASE IF NOT EXISTS ledger_db;

INSERT IGNORE INTO accounts (
    id,
    account_number,
    owner_id,
    owner_type,
    account_type,
    currency_code,
    status,
    system_code,
    created_at,
    updated_at
)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'ACC-SYS-CASH-SDG',
    NULL,
    'SYSTEM',
    'SYSTEM',
    'SDG',
    'ACTIVE',
    'CASH',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT IGNORE INTO balances (
    account_id,
    available_balance,
    blocked_balance,
    version,
    updated_at
)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    0.00,
    0.00,
    0,
    CURRENT_TIMESTAMP
);


-- this table is used to initialize the journal sequence for the first time, and to ensure that the sequence is not reset if the database is dropped and recreated
INSERT IGNORE INTO journal_sequence (
    id,
    next_value
)
VALUES (
    1,
    1
);