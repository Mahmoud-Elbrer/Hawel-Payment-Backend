package com.hawel.identity_service.entity;

import com.hawel.identity_service.constants.DeviceOS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(
        name = "devices",
        indexes = {

                @Index(
                        name = "idx_devices_user_id",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_devices_device_uuid",
                        columnList = "device_uuid",
                        unique = true
                )

        }
)
public class Device {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

//    @Column(name = "user_id", nullable = false)
//    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_uuid", nullable = false, unique = true)
    private String deviceUuid;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "device_model", nullable = false)
    private String deviceModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "os", nullable = false)
    private DeviceOS os;

    @Column(name = "app_version", nullable = false)
    private String appVersion;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "trusted", nullable = false)
    private Boolean trusted = false;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastSeen = LocalDateTime.now();
    }

}