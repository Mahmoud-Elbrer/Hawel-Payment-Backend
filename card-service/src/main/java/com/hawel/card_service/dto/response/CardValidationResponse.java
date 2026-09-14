package com.hawel.card_service.dto.response;

import com.hawel.card_service.enums.CardStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardValidationResponse {

    private boolean valid;

    private UUID cardId;

    private UUID walletId;

    private String cardNumber;

    private CardStatus status;
}