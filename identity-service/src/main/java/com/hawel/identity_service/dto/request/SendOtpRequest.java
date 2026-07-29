package com.hawel.identity_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request to send OTP to customer phone number")
public class SendOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Phone number must be in international format. Example: +249912345678"
    )
    @Schema(
            example = "+249912345678",
            description = "Customer phone number in E.164 format"
    )
    private String phoneNumber;

}