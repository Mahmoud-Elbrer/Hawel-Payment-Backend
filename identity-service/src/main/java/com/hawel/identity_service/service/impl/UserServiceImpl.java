package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.entity.User;
import com.hawel.identity_service.exception.ResourceNotFoundException;
import com.hawel.identity_service.repository.UserRepository;
import com.hawel.identity_service.service.UserService;
import com.hawel.identity_service.constants.UserStatus;
import com.hawel.identity_service.constants.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }


    // This method finds a user by phone number.
    // If the user does not exist, it creates a new customer user with the provided phone number and saves it to the database.
    // we use this for otp login, if the user is not found, we create a new user with the phone number and return it.
    @Override
    public User findOrCreateCustomer(VerifyOtpRequest request) {
        return userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> {

                    log.info("Creating new customer. phone={}", request.getPhoneNumber());

                    User user = new User();

                    user.setUserType(UserType.CUSTOMER);
                    user.setPhoneNumber(request.getPhoneNumber());
                    user.setPhoneVerified(true);
                    user.setStatus(UserStatus.ACTIVE);
                    user.setCreatedAt(LocalDateTime.now());

                    return userRepository.save(user);

                });
    }

    @Override
    public void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
