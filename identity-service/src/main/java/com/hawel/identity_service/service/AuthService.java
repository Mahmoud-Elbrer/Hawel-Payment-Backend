package com.hawel.identity_service.service;

import com.hawel.identity_service.constants.UserType;
import com.hawel.identity_service.dto.request.RefreshTokenRequest;
import com.hawel.identity_service.dto.request.SendOtpRequest;
import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.dto.response.AuthResponse;

public interface AuthService {

    /**
     * Generate and send OTP to customer's phone number.
     *
     * @param request Send OTP request
     */
    void sendOtp(SendOtpRequest request);

    /**
     * Verify OTP.
     * If customer does not exist, create account automatically.
     * Generate Access Token & Refresh Token.
     *
     * @param request Verify OTP request
     * @return Authentication response
     */
    AuthResponse verifyOtp(VerifyOtpRequest request , UserType userType);

    /**
     * Generate a new Access Token using Refresh Token.
     *
     * @param request Refresh token request
     * @return Authentication response
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logout current session.
     * Revoke refresh token.
     *
     * @param authorizationHeader Bearer access token
     */
    void logout(String authorizationHeader);
}
