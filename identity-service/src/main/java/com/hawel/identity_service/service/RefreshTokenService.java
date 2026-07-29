package com.hawel.identity_service.service;

import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.entity.RefreshToken;
import com.hawel.identity_service.security.jwt.TokenPair;
import com.hawel.identity_service.entity.User;

import java.util.UUID;

public interface RefreshTokenService {

    /**
     * Save refresh token hash.
     */
    void save(User user, Device device, TokenPair tokenPair);


    /**
     * Validate refresh token.
     */
    RefreshToken validate(String refreshToken);


    /**
     * Revoke specific refresh token.
     */
    void revoke(String refreshToken);


    /**
     * Revoke all user refresh tokens.
     */
    void revokeAllUserTokens(UUID userId);

    void revokeByUserAndDevice(
            UUID userId,
            UUID deviceId
    );

    RefreshToken rotate(
            User user,
            Device device,
            String oldRefreshToken,
            TokenPair newTokenPair
    );

}
