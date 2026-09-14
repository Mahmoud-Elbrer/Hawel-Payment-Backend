package com.hawel.card_service.dto.response;

import com.hawel.card_service.enums.CardStatus;
import com.hawel.card_service.enums.CardType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {

    private UUID id;

    private String cardNumber;

    private UUID walletId;

    private CardType cardType;

    private CardStatus status;

    private LocalDate issueDate;

    private LocalDate expiryDate;
}
