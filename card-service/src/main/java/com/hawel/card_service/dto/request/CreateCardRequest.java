package com.hawel.card_service.dto.request;

import com.hawel.card_service.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardRequest {

    @NotBlank(message = "Card UID is required")
    private String uid;

    @NotNull(message = "Wallet ID is required")
    private UUID walletId;

    @NotNull(message = "Card type is required")
    private CardType cardType;
}