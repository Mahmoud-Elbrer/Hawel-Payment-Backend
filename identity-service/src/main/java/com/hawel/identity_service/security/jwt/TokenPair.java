package com.hawel.identity_service.security.jwt;

import lombok.Builder;

@Builder
public record TokenPair(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}