package com.hawel.card_service.repository;

import com.hawel.card_service.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByUid(String uid);

    boolean existsByCardNumber(String cardNumber);

    boolean existsByUid(String uid);

    boolean existsByWalletId(UUID walletId);

    @Modifying
    @Query(
            "UPDATE Card c " +
                    "SET c.status = com.hawel.card_service.enums.CardStatus.EXPIRED " +
                    "WHERE c.status = com.hawel.card_service.enums.CardStatus.ACTIVE " +
                    "AND c.expiryDate < :today"
    )
    int expireCards(@Param("today") LocalDate today);
}