package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Override
    public void sendOtp(String phoneNumber, String otp) {

        log.info(
                "Sending OTP {} to {}",
                otp,
                phoneNumber
        );

        // TODO: Integrate with SMS provider

    }
}
