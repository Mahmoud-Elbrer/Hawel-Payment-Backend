package com.hawel.identity_service.repository;




import com.hawel.identity_service.constants.UserType;
import com.hawel.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByPhoneNumberAndUserType(String email , UserType userType);
	Optional<User> findByEmail(String email);

	Boolean existsByPhoneNumber(String username);
	Boolean existsByEmail(String username);


}
