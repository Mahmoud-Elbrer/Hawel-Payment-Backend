package com.hawel.wallet_service.repository;

import com.hawel.wallet_service.entity.WalletSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface WalletSequenceRepository extends JpaRepository<WalletSequence, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletSequence w WHERE w.sequenceName = :name")
    WalletSequence findForUpdate(@Param("name") String name);

}