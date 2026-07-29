package com.hawel.identity_service.service;

public interface SmsService {
    void sendOtp(
            String phoneNumber,
            String otp
    );

}
