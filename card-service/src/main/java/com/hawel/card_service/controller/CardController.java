package com.hawel.card_service.controller;

import com.hawel.card_service.dto.request.CreateCardRequest;
import com.hawel.card_service.dto.request.ReplaceCardRequest;
import com.hawel.card_service.dto.response.CardResponse;
import com.hawel.card_service.dto.response.CardValidationResponse;
import com.hawel.card_service.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(request));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable UUID cardId) {

        return ResponseEntity.ok(cardService.getCardById(cardId));
    }

    @GetMapping("/uid/{uid}")
    public ResponseEntity<CardResponse> getCardByUid(@PathVariable String uid) {

        return ResponseEntity.ok(cardService.getCardByUid(uid));
    }


    @PutMapping("/{cardId}/activate")
    public ResponseEntity<CardResponse> activateCard(@PathVariable UUID cardId) {

        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    @PutMapping("/{cardId}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable UUID cardId) {

        return ResponseEntity.ok(cardService.blockCard(cardId));
    }

    @PutMapping("/{cardId}/unblock")
    public ResponseEntity<CardResponse> unblockCard(@PathVariable UUID cardId) {

        return ResponseEntity.ok(cardService.unblockCard(cardId));
    }


    // when a card is lost or stolen, the user can request a replacement card. The old card will be blocked and a new card will be issued with a new UID.
    @PutMapping("/{cardId}/replace")
    public ResponseEntity<CardResponse> replaceCard(@PathVariable UUID cardId, @Valid @RequestBody ReplaceCardRequest request) {

        return ResponseEntity.ok(cardService.replaceCard(cardId, request.getNewUid()));
    }

    @PutMapping("/{cardId}/close")
    public ResponseEntity<CardResponse> closeCard(@PathVariable UUID cardId) {

        return ResponseEntity.ok(cardService.closeCard(cardId));
    }


    // this for NFC Lookup
    @GetMapping("/uid/{uid}/validate")
    public ResponseEntity<CardValidationResponse> validateCardByUid(@PathVariable String uid) {

        return ResponseEntity.ok(cardService.validateCardByUid(uid));
    }

}