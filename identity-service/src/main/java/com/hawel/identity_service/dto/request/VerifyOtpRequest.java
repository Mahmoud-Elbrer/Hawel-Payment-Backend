package com.hawel.identity_service.dto.request;

import com.hawel.identity_service.constants.DeviceOS;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to verify OTP")
public class VerifyOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Phone number must be in international format"
    )
    @Schema(example = "+249912345678")
    private String phoneNumber;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @Schema(example = "123456")
    private String otp;

    // Device Information

    @NotBlank(message = "Device UUID is required")
    @Schema(example = "7ddad9d2-7425-4d67-9245-d70b552accc3")
    private String deviceUuid;

    @Schema(example = "Samsung Galaxy S24")
    private String deviceName;

    @Schema(example = "SM-S921B")
    private String deviceModel;

    @NotNull(message = "Device OS is required")
    @Schema(example = "ANDROID")
    private DeviceOS os;

    @Schema(example = "1.0.0")
    private String appVersion;


    @Schema(example = "fcm-token-example")
    private String fcmToken;

    // Security Information

    @Schema(example = "192.168.1.10")
    private String ipAddress;


    @Schema(example = "Mozilla/5.0 Android")
    private String userAgent;


}