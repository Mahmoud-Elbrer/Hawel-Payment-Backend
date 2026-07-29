package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.config.JwtProperties;
import com.hawel.identity_service.constants.TokenType;
import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.security.jwt.TokenPair;
import com.hawel.identity_service.entity.User;
import com.hawel.identity_service.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {


    private static final String DEVICE_ID = "deviceId";
    private static final String USER_TYPE = "userType";
    private static final String TOKEN_TYPE = "tokenType";

    private static final String ACCESS_TOKEN = "ACCESS";
    private static final String REFRESH_TOKEN = "REFRESH";


    private final JwtProperties jwtProperties;

    @Override
    public TokenPair generate(User user, Device device) {

        log.debug(
                "Generating JWT tokens for userId={} deviceId={}",
                user.getId(),
                device.getId()
        );


        String accessToken = buildAccessToken(user, device);


        String refreshToken = buildRefreshToken(user, device);


        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .build();
    }

    private String buildAccessToken(User user, Device device) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtProperties.getAccessTokenExpiration())))
                .claim(DEVICE_ID, device.getId().toString())
                .claim(USER_TYPE, user.getUserType().name())
                .claim(TOKEN_TYPE, ACCESS_TOKEN)
                .signWith(getSigningKey())
                .compact();

    }


    private String buildRefreshToken(User user, Device device) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtProperties.getRefreshTokenExpiration())))
                .claim(DEVICE_ID, device.getId().toString())
                .claim(TOKEN_TYPE, REFRESH_TOKEN)
                .signWith(getSigningKey())
                .compact();

    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());

        return Keys.hmacShaKeyFor(keyBytes);

    }


    @Override
    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    @Override
    public UUID extractDeviceId(String token) {

        String deviceId = extractClaims(token).get(DEVICE_ID, String.class);

        return UUID.fromString(deviceId);

    }


    @Override
    public boolean isValid(String token) {
        try {

            Claims claims = extractClaims(token);

            return claims.getIssuer().equals(jwtProperties.getIssuer()) && claims.getExpiration().after(new Date()) && claims.getSubject() != null;

        } catch (JwtException | IllegalArgumentException exception) {

            log.warn("JWT validation failed: {}", exception.getMessage());

            return false;

        }
    }

    @Override
    public boolean isExpired(String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());

    }

    @Override
    public TokenType extractTokenType(String token) {
        return  TokenType.valueOf(extractClaims(token).get(TOKEN_TYPE, String.class));
    }


    private Claims extractClaims(String token) {

        return Jwts.parser().verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
