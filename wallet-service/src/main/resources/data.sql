CREATE DATABASE IF NOT EXISTS wallet_db;

INSERT INTO wallet_sequence (
    sequence_name,
    current_value
)
VALUES (
    'WALLET',
    1490000
)
ON DUPLICATE KEY UPDATE
current_value = current_value;