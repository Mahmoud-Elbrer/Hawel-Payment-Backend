package com.hawel.identity_service.repository;

import com.hawel.identity_service.entity.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTPCode, Long> {

    Optional<OTPCode> findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);


}
