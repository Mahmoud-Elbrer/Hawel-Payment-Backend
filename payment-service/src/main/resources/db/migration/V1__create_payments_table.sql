CREATE TABLE payments (
    id UUID PRIMARY KEY,

    payment_reference VARCHAR(100) NOT NULL UNIQUE,

    payer_wallet_id UUID,

    receiver_wallet_id UUID NOT NULL,

    amount DECIMAL(19,4) NOT NULL,

    currency_code VARCHAR(3) NOT NULL,

    payment_method VARCHAR(50) NOT NULL,

    status VARCHAR(50) NOT NULL,

    transaction_id UUID,

    description VARCHAR(500),

    expires_at TIMESTAMP WITH TIME ZONE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_payments_reference
    ON payments(payment_reference);

CREATE INDEX idx_payments_status
    ON payments(status);

CREATE INDEX idx_payments_receiver_wallet
    ON payments(receiver_wallet_id);

CREATE INDEX idx_payments_transaction
    ON payments(transaction_id);