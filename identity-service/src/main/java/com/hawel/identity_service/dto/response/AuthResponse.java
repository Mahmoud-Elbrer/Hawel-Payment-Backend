package com.hawel.identity_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Authentication response")
public class AuthResponse {

    @Schema(example = "9f7f70bc-59b5-4b31-b812-73e598dcb0af")
    private UUID userId;

    @Schema(example = "CUSTOMER")
    private String userType;

    @Schema(example = "+249912345678")
    private String phoneNumber;

    @Schema(description = "JWT Access Token")
    private String accessToken;

    @Schema(description = "JWT Refresh Token")
    private String refreshToken;

    @Schema(example = "Bearer")
    private String tokenType;

    @Schema(example = "3600")
    private Long expiresIn;

    private LocalDateTime loginAt;

}
