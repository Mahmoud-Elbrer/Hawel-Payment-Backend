package com.hawel.identity_service.entity;

import com.hawel.identity_service.constants.UserStatus;
import com.hawel.identity_service.constants.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
		name = "users",
		indexes = {
				@Index(name = "idx_users_phone", columnList = "phone_number"),
				@Index(name = "idx_users_email", columnList = "email"),
				@Index(name = "idx_users_type", columnList = "user_type")
		}
)
public class User extends BaseEntity{

	@Id
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_type", nullable = false)
	private UserType userType;

	@Column(name = "phone_number", unique = true , length = 20)
	private String phoneNumber;

	@Column(name = "email", unique = true , length = 150)
	private String email;

	@Column(name = "password_hash" )
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private UserStatus status = UserStatus.PENDING;

	@Column(name = "phone_verified", nullable = false)
	private boolean phoneVerified = false;

	@Column(name = "email_verified", nullable = false)
	private boolean emailVerified = false;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

}