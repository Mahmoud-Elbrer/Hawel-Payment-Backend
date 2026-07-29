package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.dto.request.DeviceRequest;
import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.entity.User;
import com.hawel.identity_service.exception.ResourceNotFoundException;
import com.hawel.identity_service.repository.DeviceRepository;
import com.hawel.identity_service.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    @Override
    @Transactional
    public Device register(User user, DeviceRequest request) {

        log.info("Registering device. userId={}, deviceUuid={}", user.getId(), request.getDeviceUuid());

        Device device = deviceRepository.findByDeviceUuid(request.getDeviceUuid()).map(existingDevice -> {
            log.info("Existing device found. deviceId={}", existingDevice.getId());
            existingDevice.setDeviceName(request.getDeviceName());
            existingDevice.setDeviceModel(request.getDeviceModel());
            existingDevice.setOs(request.getOs());
            existingDevice.setAppVersion(request.getAppVersion());
            existingDevice.setFcmToken(request.getFcmToken());
            existingDevice.setIpAddress(request.getIpAddress());
            existingDevice.setUserAgent(request.getUserAgent());
            existingDevice.setLastSeen(LocalDateTime.now());
            existingDevice.setActive(true);
            return existingDevice;

        }).orElseGet(() -> {
            log.info("Creating new device for userId={}", user.getId());
            Device newDevice = Device.builder().user(user).deviceUuid(request.getDeviceUuid()).deviceName(request.getDeviceName()).deviceModel(request.getDeviceModel()).os(request.getOs()).appVersion(request.getAppVersion()).fcmToken(request.getFcmToken()).ipAddress(request.getIpAddress()).userAgent(request.getUserAgent()).trusted(false).active(true).lastSeen(LocalDateTime.now()).build();

            return newDevice;

        });

        Device saved = deviceRepository.save(device);


        log.info("Device registered successfully. deviceId={}", saved.getId());

        return saved;

    }

    @Override
    @Transactional(readOnly = true)
    public Device findById(UUID deviceId) {

        return deviceRepository.findByIdAndActiveTrue(deviceId).orElseThrow(() -> new ResourceNotFoundException("Device not found", " ID ", deviceId));
    }

    @Override
    @Transactional(readOnly = true)
    public Device findByDeviceUuid(String deviceUuid) {
        return deviceRepository.findByDeviceUuid(deviceUuid).orElseThrow(() -> {
            log.warn("Device not found. deviceUuid={}", deviceUuid);

            return new ResourceNotFoundException("Device not found with UUID: ", " ID ", UUID.fromString(deviceUuid));

        });
    }

    @Override
    public void updateLastSeen(Device device) {
        device.setLastSeen(LocalDateTime.now());

        deviceRepository.save(device);

        log.debug("Device last seen updated. deviceId={}", device.getId());
    }

    @Override
    public List<Device> getUserDevices(UUID userId) {

        log.info("Fetching devices for userId={}", userId);

        return deviceRepository.findAllByUserIdAndActiveTrue(userId);

    }

    @Override
    public void deactivate(UUID deviceId) {

        Device device = findById(deviceId);

        device.setActive(false);

        deviceRepository.save(device);

        log.info("Device deactivated. deviceId={}", deviceId);

    }
}
