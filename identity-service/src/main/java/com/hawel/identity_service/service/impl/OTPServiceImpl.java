package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.entity.OTPCode;
import com.hawel.identity_service.exception.IdentityApiException;
import com.hawel.identity_service.repository.OTPRepository;
import com.hawel.identity_service.service.OTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;
    private static final int OTP_EXPIRE_MINUTES = 5;


    private final PasswordEncoder passwordEncoder;

    private final OTPRepository otpRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateOtp() {
        // int otp = 100000 + new Random().nextInt(900000);
        // it's better to use SecureRandom for generating OTPs for better security
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public String hashOtp(String otp) {
        return passwordEncoder.encode(otp);
    }

    @Override
    public OTPCode verifyOtp(VerifyOtpRequest request) {

        // Find the most recent OTP for the provided phone number
        OTPCode otp = otpRepository
                .findTopByPhoneNumberOrderByCreatedAtDesc(request.getPhoneNumber())
                .orElseThrow(() -> new IdentityApiException(HttpStatus.BAD_REQUEST, "No OTP found for the provided phone number."));

        // validate the OTP (check if it's expired, already verified, or attempts exceeded)
        validateOtp(otp);

        // Check if the provided OTP matches the stored hashed OTP
        if (!passwordEncoder.matches(request.getOtp(), otp.getOtpCodeHash())) {

            otp.setAttempts(otp.getAttempts() + 1);

            otpRepository.save(otp);

            throw new IdentityApiException(HttpStatus.BAD_REQUEST, "Invalid OTP provided.");
        }

        otp.setVerified(true);

        otp.setUsedAt(LocalDateTime.now());

        otpRepository.save(otp);

        return otp;
    }

    @Override
    public boolean canSendOtp(String phoneNumber) {
        return otpRepository
                .findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .map(otp -> otp.getCreatedAt().plusSeconds(60).isBefore(LocalDateTime.now()))
                .orElse(true);
    }

    @Override
    public void validateOtp(OTPCode otpCode) {

        if (Boolean.TRUE.equals(otpCode.getVerified())) {

            throw new IdentityApiException(HttpStatus.BAD_REQUEST, "OTP has already been verified.");
        }

        if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new IdentityApiException(HttpStatus.BAD_REQUEST, "OTP has expired.");
        }

        if (otpCode.getAttempts() >= MAX_ATTEMPTS) {

            throw new IdentityApiException(HttpStatus.BAD_REQUEST, "Maximum OTP verification attempts exceeded.");
        }

    }

    @Override
    public boolean resendOtp(String phoneNumber) {
        return false;
    }

    @Override
    public boolean deleteExpiredOtp(String phoneNumber) {
        return false;
    }
}
