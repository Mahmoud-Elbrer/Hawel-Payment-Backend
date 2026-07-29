package com.hawel.wallet_service.kafka;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String WALLET_CREATED = "wallet.created";

    public static final String WALLET_ACTIVATED = "wallet.activated";

    public static final String WALLET_FROZEN = "wallet.frozen";

    public static final String WALLET_UNFROZEN = "wallet.unfrozen";

    public static final String WALLET_CLOSED = "wallet.closed";

    public static final String WALLET_LIMIT_CHANGED = "wallet.limit.changed";
    public static final String WALLET_SUSPENDED = "wallet.limit.suspended";
    public static final String WALLET_SETTINGS_CHANGED = "wallet.settings.changed";
}
