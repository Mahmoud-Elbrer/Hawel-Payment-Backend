package com.hawel.identity_service.dto.request;

import com.hawel.identity_service.constants.DeviceOS;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequest {

    private String deviceUuid;

    private String deviceName;

    private String deviceModel;

    private DeviceOS os;

    private String appVersion;

    private String fcmToken;

    private String ipAddress;

    private String userAgent;

}