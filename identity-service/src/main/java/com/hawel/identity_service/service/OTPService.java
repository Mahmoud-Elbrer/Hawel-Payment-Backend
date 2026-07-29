package com.hawel.identity_service.service;

import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.entity.OTPCode;

public interface OTPService {
    String generateOtp();


    String hashOtp(String otp);


    OTPCode verifyOtp(VerifyOtpRequest request);


    boolean canSendOtp(String phoneNumber);

    /**
     * Validate OTP business rules.
     *
     * Checks:
     * - Exists
     * - Not expired
     * - Not verified
     * - Attempts limit
     *
     * @param otpCode Stored OTP
     */
    void validateOtp(OTPCode otpCode);

    boolean resendOtp(String phoneNumber);

    boolean deleteExpiredOtp(String phoneNumber);
}