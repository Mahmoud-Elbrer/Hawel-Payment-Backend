package com.hawel.identity_service.mapper;

import com.hawel.identity_service.dto.response.AuthResponse;
import com.hawel.identity_service.security.jwt.TokenPair;
import com.hawel.identity_service.entity.User;
import org.springframework.stereotype.Component;


@Component
public class AuthMapper {


    public AuthResponse toResponse(
            User user,
            TokenPair tokenPair
    ) {


        return AuthResponse.builder()

                .accessToken(
                        tokenPair.accessToken()
                )

                .refreshToken(
                        tokenPair.refreshToken()
                )

                .tokenType(
                        "Bearer"
                )

                .userId(
                        user.getId()
                )

                .phoneNumber(
                        user.getPhoneNumber()
                )

                .build();

    }

}