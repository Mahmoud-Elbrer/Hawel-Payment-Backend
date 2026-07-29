package com.hawel.identity_service.service;

import com.hawel.identity_service.constants.TokenType;
import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.entity.User;
import com.hawel.identity_service.security.jwt.TokenPair;

import java.util.UUID;

public interface JwtService {

    /**
     * Generate access and refresh tokens.
     */
    TokenPair generate(User user, Device device);

    UUID extractUserId(String token);

    UUID extractDeviceId(String token);

    boolean isValid(String token);

    boolean isExpired(String token);

    TokenType extractTokenType(String token);

}
