package com.hawel.card_service.service;

import com.hawel.card_service.dto.request.CreateCardRequest;
import com.hawel.card_service.dto.response.CardResponse;
import com.hawel.card_service.dto.response.CardValidationResponse;

import java.util.UUID;

public interface CardService {

    CardResponse createCard(CreateCardRequest request);

    CardResponse getCardById(UUID cardId);

    CardResponse getCardByUid(String uid);

    CardResponse activateCard(UUID cardId);

    CardResponse blockCard(UUID cardId);

    CardResponse unblockCard(UUID cardId);

    CardResponse replaceCard(UUID cardId, String newUid);

    CardResponse closeCard(UUID cardId);

    // we need this in Payment Service to validate the card before processing the payment. The card is valid if it exists and is active.
    // this for NFC Lookup
    CardValidationResponse validateCardByUid(String uid);

}
