package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.config.JwtProperties;
import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.entity.RefreshToken;
import com.hawel.identity_service.security.jwt.TokenPair;
import com.hawel.identity_service.entity.User;
import com.hawel.identity_service.exception.IdentityApiException;
import com.hawel.identity_service.repository.RefreshTokenRepository;
import com.hawel.identity_service.service.RefreshTokenService;
import com.hawel.identity_service.service.TokenHashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    private final TokenHashService tokenHashService;

    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public void save(User user, Device device, TokenPair tokenPair) {

        // Hash the refresh token before saving it to the database for security reasons.
        String hash = tokenHashService.hash(tokenPair.refreshToken());

        LocalDateTime now = LocalDateTime.now();

        // Create a new RefreshToken entity and set its properties.
        RefreshToken refreshToken = RefreshToken.builder().user(user).device(device).tokenHash(hash).revoked(false)
                .expiresAt(
                        now.plus(
                                Duration.ofMillis(
                                        jwtProperties.getRefreshTokenExpiration()
                                )
                        )
                )
                .createdAt(LocalDateTime.now()).build();

        repository.save(refreshToken);

        log.info("Refresh token saved userId={} deviceId={}", user.getId(), device.getId());

    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validate(String refreshToken) {

        // Hash the provided refresh token to compare it with the stored hash in the database.
        String hash = tokenHashService.hash(refreshToken);

        // Find the refresh token in the database by its hash. If not found, throw an exception.
        RefreshToken token = repository.findByTokenHash(hash).orElseThrow(() -> new IdentityApiException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        if (Boolean.TRUE.equals(token.getRevoked())) {

            log.warn("Refresh token revoked userId={} deviceId={}", token.getUser().getId(), token.getDevice().getId());

            throw new IdentityApiException(HttpStatus.UNAUTHORIZED, "Refresh token revoked");
        }


        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {

            log.warn("Refresh token expired userId={} deviceId={}", token.getUser().getId(), token.getDevice().getId());

            throw new IdentityApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        return token;

    }

    @Override
    @Transactional
    public void revoke(String refreshToken) {
        String hash = tokenHashService.hash(refreshToken);


        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() ->
                        new IdentityApiException(
                                HttpStatus.UNAUTHORIZED,
                                "Refresh token not found"
                        )
                );

        if (!Boolean.TRUE.equals(token.getRevoked())) {

            token.setRevoked(true);

            repository.save(token);

            log.info("Refresh token revoked. tokenId={}", token.getId());
        }

    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UUID userId) {

        List<RefreshToken> tokens = repository.findAllByUser_Id(userId);

        tokens.forEach(token -> token.setRevoked(true));

        repository.saveAll(tokens);

        log.info("Revoked all refresh tokens for userId={}", userId);
    }

    @Override
    @Transactional
    public void revokeByUserAndDevice(UUID userId, UUID deviceId) {

        List<RefreshToken> tokens = repository.findAllByUser_IdAndDevice_IdAndRevokedFalse(userId, deviceId);

        tokens.forEach(token -> token.setRevoked(true));

        repository.saveAll(tokens);

        log.info("Refresh tokens revoked. userId={}, deviceId={}, count={}", userId, deviceId, tokens.size());

    }

    @Override
    @Transactional
    public RefreshToken rotate(User user, Device device, String oldRefreshToken, TokenPair newTokenPair) {

        RefreshToken currentToken = validate(oldRefreshToken);

        if (!currentToken.getUser().getId().equals(user.getId()) || !currentToken.getDevice().getId().equals(device.getId())) {

            throw new IdentityApiException(HttpStatus.UNAUTHORIZED, "Refresh token does not belong to this user or device");
        }

        currentToken.setRevoked(true);

        repository.save(currentToken);

        LocalDateTime now = LocalDateTime.now();

        String hash = tokenHashService.hash(newTokenPair.refreshToken());

        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .device(device)
                .tokenHash(hash)
                .revoked(false)
                .expiresAt(now.plus(Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()))).createdAt(LocalDateTime.now())
                .build();

        RefreshToken saved = repository.save(newToken);

        log.info("Refresh token rotated. userId={}, deviceId={}", user.getId(), device.getId());

        return saved;
    }
}
