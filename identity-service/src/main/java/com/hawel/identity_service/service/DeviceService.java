package com.hawel.identity_service.service;

import com.hawel.identity_service.dto.request.DeviceRequest;
import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.entity.User;

import java.util.List;
import java.util.UUID;

public interface DeviceService {
    Device register(
            User user,
            DeviceRequest request
    );

    Device findById(
            UUID deviceId
    );

    Device findByDeviceUuid(String deviceUuid);

    void updateLastSeen(Device device);

    List<Device> getUserDevices(UUID userId);

    void deactivate(UUID deviceId);
}
