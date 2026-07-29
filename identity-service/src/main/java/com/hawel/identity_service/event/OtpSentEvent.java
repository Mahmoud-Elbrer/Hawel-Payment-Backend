package com.hawel.identity_service.event;

import com.hawel.identity_service.constants.OtpChannel;
import com.hawel.identity_service.constants.OtpPurpose;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter

public class OtpSentEvent extends BaseEvent {

    private final String phoneNumber;
    private final OtpPurpose purpose;
    private final OtpChannel channel;
    private final LocalDateTime expiresAt;


    public OtpSentEvent(String phoneNumber, OtpPurpose purpose, OtpChannel channel, LocalDateTime expiresAt) {
        super();

        this.phoneNumber = phoneNumber;
        this.purpose = purpose;
        this.channel = channel;
        this.expiresAt = expiresAt;
    }
}
