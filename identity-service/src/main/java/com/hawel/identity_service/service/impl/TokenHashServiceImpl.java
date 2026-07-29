package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.exception.IdentityApiException;
import com.hawel.identity_service.service.TokenHashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


@Service
@Slf4j
public class TokenHashServiceImpl implements TokenHashService {

    @Override
    public String hash(String token) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new IdentityApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error hashing token");
        }

    }
}
