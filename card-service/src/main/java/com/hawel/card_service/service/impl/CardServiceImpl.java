package com.hawel.card_service.service.impl;

import com.hawel.card_service.client.WalletClient;
import com.hawel.card_service.client.dto.WalletResponse;
import com.hawel.card_service.dto.request.CreateCardRequest;
import com.hawel.card_service.dto.response.CardResponse;
import com.hawel.card_service.dto.response.CardValidationResponse;
import com.hawel.card_service.entity.Card;
import com.hawel.card_service.enums.CardStatus;
import com.hawel.card_service.repository.CardRepository;
import com.hawel.card_service.service.CardService;
import com.hawel.common_service.enums.WalletStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final WalletClient walletClient;

    @Override
    public CardResponse createCard(CreateCardRequest request) {

        log.info("Creating card for walletId={}, uid={}, cardType={}", request.getWalletId(), request.getUid(), request.getCardType());

        // 1. Check UID
        if (cardRepository.existsByUid(request.getUid())) {
            log.warn("Card creation failed. UID already exists: {}", request.getUid());
            throw new IllegalArgumentException("Card UID already exists");
        }

        // 2. Get wallet from Wallet Service
        log.info("Validating wallet. walletId={}", request.getWalletId());

        WalletResponse wallet;

        try {

            // Call Wallet Service to get wallet details
            wallet = walletClient.getWallet(request.getWalletId());

        } catch (Exception e) {

            log.error("Failed to retrieve wallet. walletId={}", request.getWalletId(), e);

            throw new IllegalArgumentException("Wallet not found or Wallet Service is unavailable");
        }

        // 3. Check wallet status
        if (wallet.getStatus() != WalletStatus.ACTIVE) {

            log.warn("Card creation failed. Wallet is not active. walletId={}, status={}", wallet.getId(), wallet.getStatus());

            throw new IllegalArgumentException("Card can only be linked to an active wallet");
        }


        // 4. Generate card number
        String cardNumber = generateCardNumber();

        // 5. Create card
        Card card = Card.builder()
                .cardNumber(cardNumber)
                .uid(request.getUid())
                .walletId(request.getWalletId())
                .cardType(request.getCardType())
                .status(CardStatus.ISSUED)
                .issueDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(3)) // todo : make configurable
                .build();

        Card savedCard = cardRepository.save(card);

        log.info("Card created successfully. cardId={}, cardNumber={}, walletId={}", savedCard.getId(), savedCard.getCardNumber(), savedCard.getWalletId());

        return mapToResponse(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getCardById(UUID cardId) {

        log.info("Fetching card by id={}", cardId);

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        return mapToResponse(card);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getCardByUid(String uid) {

        log.info("Fetching card by UID={}", uid);

        Card card = cardRepository.findByUid(uid).orElseThrow(() -> new IllegalArgumentException("Card not found for UID: " + uid));

        return mapToResponse(card);
    }

    @Override
    public CardResponse activateCard(UUID cardId) {
        log.info("Activating card. cardId={}", cardId);

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        if (card.getStatus() != CardStatus.ISSUED) {

            log.warn("Card activation failed. cardId={}, currentStatus={}", cardId, card.getStatus());

            throw new IllegalArgumentException("Only ISSUED cards can be activated");
        }

        card.setStatus(CardStatus.ACTIVE);

        Card savedCard = cardRepository.save(card);

        log.info("Card activated successfully. cardId={}, status={}", savedCard.getId(), savedCard.getStatus());

        return mapToResponse(savedCard);
    }

    @Override
    public CardResponse blockCard(UUID cardId) {
        log.info("Blocking card. cardId={}", cardId);

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        if (card.getStatus() != CardStatus.ACTIVE) {

            log.warn("Card blocking failed. cardId={}, currentStatus={}", cardId, card.getStatus());

            throw new IllegalArgumentException("Only ACTIVE cards can be blocked");
        }

        card.setStatus(CardStatus.BLOCKED);

        Card savedCard = cardRepository.save(card);

        log.info("Card blocked successfully. cardId={}, status={}", savedCard.getId(), savedCard.getStatus());

        return mapToResponse(savedCard);
    }

    @Override
    public CardResponse unblockCard(UUID cardId) {
        log.info("Unblocking card. cardId={}", cardId);

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        if (card.getStatus() != CardStatus.BLOCKED) {

            log.warn("Card unblocking failed. cardId={}, currentStatus={}", cardId, card.getStatus());

            throw new IllegalArgumentException("Only BLOCKED cards can be unblocked");
        }

        card.setStatus(CardStatus.ACTIVE);

        Card savedCard = cardRepository.save(card);

        log.info("Card unblocked successfully. cardId={}, status={}", savedCard.getId(), savedCard.getStatus());

        return mapToResponse(savedCard);
    }


    // when a card is lost or block, the user can request a replacement card. The old card will be blocked and a new card will be issued with a new UID.
    @Override
    public CardResponse replaceCard(UUID cardId, String newUid) {

        log.info("Replacing card. cardId={}, newUid={}", cardId, newUid);

        Card oldCard = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        if (oldCard.getStatus() != CardStatus.ACTIVE && oldCard.getStatus() != CardStatus.BLOCKED) {

            log.warn("Card replacement failed. cardId={}, status={}", cardId, oldCard.getStatus());

            throw new IllegalArgumentException("Only ACTIVE or BLOCKED cards can be replaced");
        }

        if (cardRepository.existsByUid(newUid)) {

            log.warn("Card replacement failed. UID already exists: {}", newUid);

            throw new IllegalArgumentException("New card UID already exists");
        }

        // Old card
        oldCard.setStatus(CardStatus.REPLACED);
        cardRepository.save(oldCard);

        // New card
        Card newCard = Card.builder()
                .cardNumber(generateCardNumber())
                .uid(newUid)
                .walletId(oldCard.getWalletId())
                .cardType(oldCard.getCardType())
                .status(CardStatus.ISSUED)
                .issueDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(3))
                .build();

        Card savedCard = cardRepository.save(newCard);

        log.info("Card replaced successfully. oldCardId={}, newCardId={}, walletId={}", oldCard.getId(), savedCard.getId(), savedCard.getWalletId());

        return mapToResponse(savedCard);
    }

    @Override
    public CardResponse closeCard(UUID cardId) {
        log.info("Closing card. cardId={}", cardId);

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        if (card.getStatus() != CardStatus.ACTIVE && card.getStatus() != CardStatus.BLOCKED) {

            log.warn("Card closing failed. cardId={}, currentStatus={}", cardId, card.getStatus());

            throw new IllegalArgumentException("Only ACTIVE or BLOCKED cards can be closed");
        }

        card.setStatus(CardStatus.CLOSED);

        Card savedCard = cardRepository.save(card);

        log.info("Card closed successfully. cardId={}, status={}", savedCard.getId(), savedCard.getStatus());

        return mapToResponse(savedCard);
    }


    // we need this in Payment Service to validate the card before processing the payment. The card is valid if it exists and is active.
    // this for NFC Lookup
    @Override
    @Transactional(readOnly = true)
    public CardValidationResponse validateCardByUid(String uid) {
        log.info("Validating NFC card. uid={}", uid);

        Card card = cardRepository.findByUid(uid).orElseThrow(() -> {

            log.warn("NFC card validation failed. Card not found. uid={}", uid);

            return new IllegalArgumentException("Card not found for UID: " + uid);
        });

        boolean valid = card.getStatus() == CardStatus.ACTIVE;

        log.info("NFC card validation completed. cardId={}, status={}, valid={}", card.getId(), card.getStatus(), valid);

        return CardValidationResponse.builder()
                .valid(valid)
                .cardId(card.getId())
                .walletId(card.getWalletId())
                .cardNumber(card.getCardNumber())
                .status(card.getStatus())
                .build();
    }

    private String generateCardNumber() {

        long count = cardRepository.count() + 1;

        return String.format("CARD%07d", count);
    }

    private CardResponse mapToResponse(Card card) {

        return CardResponse.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .walletId(card.getWalletId())
                .cardType(card.getCardType())
                .status(card.getStatus())
                .issueDate(card.getIssueDate())
                .expiryDate(card.getExpiryDate())
                .build();
    }
}