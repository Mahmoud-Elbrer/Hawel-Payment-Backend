package com.hawel.identity_service.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    /**
     * Base64 encoded secret key.
     */
    private String secret;

    /**
     * Access token expiration in milliseconds.
     */
    private long accessTokenExpiration;

    /**
     * Refresh token expiration in milliseconds.
     */
    private long refreshTokenExpiration;

    /**
     * JWT issuer.
     */
    private String issuer;
}
