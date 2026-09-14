package com.hawel.identity_service.service;

import com.hawel.identity_service.constants.UserType;
import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findByPhoneNumberAndUserType(String phoneNumber , UserType userType);

    Optional<User> findByEmail(String email);

    User findById(UUID id);

    User findOrCreateUser(VerifyOtpRequest request , UserType userType);

    void updateLastLogin(User user);

    User save(User user);
}
