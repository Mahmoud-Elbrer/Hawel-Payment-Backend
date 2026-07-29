package com.hawel.identity_service.entity;

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
        name = "sessions",
        indexes = {

                @Index(
                        name = "idx_sessions_user_id",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_sessions_device_id",
                        columnList = "device_id"
                ),

                @Index(
                        name = "idx_sessions_active",
                        columnList = "active"
                )

        }
)
public class Session {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

//    @Column(name = "user_id", nullable = false)
//    private UUID userId;
//
//    @Column(name = "device_id", nullable = false)
//    private UUID deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "device_id",
            nullable = false
    )
    private Device device;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "user_agent", nullable = false, columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "logout_at")
    private LocalDateTime logoutAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;


    @PrePersist
    protected void onCreate() {
        loginAt = LocalDateTime.now();

        lastActivityAt = LocalDateTime.now();
    }
}