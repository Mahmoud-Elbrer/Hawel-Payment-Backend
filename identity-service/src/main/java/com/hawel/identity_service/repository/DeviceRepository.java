package com.hawel.identity_service.repository;

import com.hawel.identity_service.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceUuid(String deviceUuid);

    List<Device> findAllByUserIdAndActiveTrue(UUID userId);

    boolean existsByDeviceUuid(String deviceUuid);

    Optional<Device> findByIdAndActiveTrue(UUID deviceId);

}
