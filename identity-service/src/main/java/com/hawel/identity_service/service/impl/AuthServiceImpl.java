package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.constants.OtpChannel;
import com.hawel.identity_service.constants.TokenType;
import com.hawel.identity_service.constants.UserType;
import com.hawel.identity_service.dto.request.DeviceRequest;
import com.hawel.identity_service.dto.request.RefreshTokenRequest;
import com.hawel.identity_service.dto.request.SendOtpRequest;
import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.dto.response.AuthResponse;
import com.hawel.identity_service.entity.*;
import com.hawel.identity_service.event.*;
import com.hawel.identity_service.exception.IdentityApiException;
import com.hawel.identity_service.mapper.AuthMapper;
import com.hawel.identity_service.repository.OTPRepository;
import com.hawel.identity_service.security.jwt.TokenPair;
import com.hawel.identity_service.service.*;
import com.hawel.identity_service.constants.OtpPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {


    private final OTPRepository otpRepository;

    private final OTPService otpService;
    private final SmsService smsService;

    private final UserService userService;
    private final DeviceService deviceService;
    private final SessionService sessionService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final AuthMapper authMapper;

    private final EventPublisher eventPublisher;


    @Override
    public void sendOtp(SendOtpRequest request) {

        log.info("Sending OTP to phone number: {}", request.getPhoneNumber());

        // 1. Check OTP Rate Limit
        boolean isAllowed = otpService.canSendOtp(request.getPhoneNumber());

        if (!isAllowed) {
            log.warn("OTP request rate limit exceeded for phone number: {}", request.getPhoneNumber());
            throw new IdentityApiException(HttpStatus.TOO_MANY_REQUESTS, "Too many OTP requests. Please try again later.");
        }

        // 2. Generate OTP
        String otp = otpService.generateOtp();

        log.debug("Generated OTP for phone number={}: {}", request.getPhoneNumber(), otp);
        log.info("OTP generated successfully for phone number={}", request.getPhoneNumber());

        // 3. Hash OTP before e saving to database
        String hashedOtp = otpService.hashOtp(otp);

        // 4. Save OTP to database
        OTPCode otpVerification = OTPCode.builder()
                .phoneNumber(request.getPhoneNumber())
                .otpCodeHash(hashedOtp)
                .purpose(OtpPurpose.LOGIN)
                .channel(OtpChannel.SMS)
                .attempts(0)
                .resendCount(0) // todo : implement resend count logic
                .verified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5))  // OTP expires in 5 minutes
                .build();

        otpRepository.save(otpVerification);

        // 5. Send OTP to user via SMS
        smsService.sendOtp(request.getPhoneNumber(), otp);

        // 6. Log the OTP sent event
        eventPublisher.publish(new OtpSentEvent(request.getPhoneNumber(), OtpPurpose.LOGIN, OtpChannel.SMS, LocalDateTime.now().plusMinutes(5)));

        log.info("OTP sent to phone number: {}", request.getPhoneNumber());

    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request  , UserType userType) {

        log.info("Verifying OTP for phone number: {}", request.getPhoneNumber());

        // 1- Verify the OTP
        otpService.verifyOtp(request);

        log.info("OTP verified successfully for phone number: {}", request.getPhoneNumber());

        // 2- Find or create the user
        User user = userService.findOrCreateUser(request , userType);

        log.info("User found or created successfully. userId={}", user.getId());

        // 3- Register the device
        DeviceRequest deviceRequest = DeviceRequest.builder()
                        .deviceUuid(request.getDeviceUuid())
                        .deviceName(request.getDeviceName())
                        .deviceModel(request.getDeviceModel())
                        .os(request.getOs())
                        .appVersion(request.getAppVersion())
                        .fcmToken(request.getFcmToken())
                        .ipAddress(request.getIpAddress())
                        .userAgent(request.getUserAgent())
                        .build();
        Device device = deviceService.register(user, deviceRequest);

        log.info("Device registered successfully. deviceId={}", device.getId());

        // 4 - Create a session for the user and device
        Session session = sessionService.create(user, device, request.getIpAddress(), request.getUserAgent());

        log.info("Session created successfully. sessionId={}", session.getId());

        // 5- Generate JWT tokens for the user and device
        TokenPair tokenPair = jwtService.generate(user, device);

        log.info("JWT tokens generated successfully for userId={} and deviceId={}", user.getId(), device.getId());

        // 6- Save the refresh token in the database
        refreshTokenService.save(user, device, tokenPair);

        // 7- Publish an event for user login
        // TODO : Publish an event for user login
        eventPublisher.publish(new UserLoggedInEvent(user.getId(), device.getId()));

        log.info("User login event published successfully for userId={} and deviceId={}", user.getId(), device.getId());

        return authMapper.toResponse(user, tokenPair);

    }


    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");

        String refreshToken = request.getRefreshToken();

        /*
         * 1. Validate JWT
         */
        if (!jwtService.isValid(refreshToken)) {

            throw new IdentityApiException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }


        /*
         * 2. Verify token type
         */
        if (jwtService.extractTokenType(refreshToken) != TokenType.REFRESH) {

            throw new IdentityApiException(HttpStatus.UNAUTHORIZED, "Invalid token type");
        }


        /*
         * 3. Validate database record
         */
        refreshTokenService.validate(refreshToken);


        /*
         * 4. Extract identities
         */
        UUID userId = jwtService.extractUserId(refreshToken);

        UUID deviceId = jwtService.extractDeviceId(refreshToken);


        /*
         * 5. Load user
         */
        User user = userService.findById(userId);


        /*
         * 6. Load device
         */
        Device device = deviceService.findById(deviceId);


        /*
         * 7. Generate new tokens
         */
        TokenPair tokenPair = jwtService.generate(user, device);


        /*
         * 8. Rotate refresh token
         */
        refreshTokenService.rotate(user, device, refreshToken, tokenPair);


        /*
         * 9. Update session activity
         */
        sessionService.updateLastActivity(userId, deviceId);


        log.info("Refresh token completed successfully. userId={}, deviceId={}", userId, deviceId);

        // TODO : Publish an event for token refresh
        eventPublisher.publish(new TokenRefreshedEvent(userId, deviceId));

        return authMapper.toResponse(user, tokenPair);
    }

    @Override
    public void logout(String authorizationHeader) {
        log.info("Logout request received.");

        String accessToken = authorizationHeader.replace("Bearer ", "");

        if (!jwtService.isValid(accessToken)) {

            throw new IdentityApiException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }

        if (jwtService.extractTokenType(accessToken) != TokenType.ACCESS) {

            throw new IdentityApiException(HttpStatus.UNAUTHORIZED, "Invalid token type");
        }

        UUID userId = jwtService.extractUserId(accessToken);

        UUID deviceId = jwtService.extractDeviceId(accessToken);

        sessionService.logout(userId, deviceId);

        refreshTokenService.revokeByUserAndDevice(userId, deviceId);

        // TODO : Publish an event for user logout
        eventPublisher.publish(new UserLoggedOutEvent(userId, deviceId));

        log.info(
                "Logout completed successfully. userId={}, deviceId={}",
                userId,
                deviceId
        );
    }
}
