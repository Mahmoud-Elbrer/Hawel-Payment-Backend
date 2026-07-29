package com.hawel.identity_service.kafka;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String USER_REGISTERED = "identity.user.registered";

    public static final String USER_LOGGED_IN = "identity.user.logged-in";

    public static final String USER_LOGGED_OUT = "identity.user.logged-out";

    public static final String PASSWORD_CHANGED = "identity.password.changed";
    public static final String TOKEN_REFRESHED = "identity.token.refreshed";
    public static final String OTP_SENT = "identity.otp.sent";
}
