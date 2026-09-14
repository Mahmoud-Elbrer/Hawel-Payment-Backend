package com.hawel.card_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplaceCardRequest {

    @NotBlank(message = "New card UID is required")
    private String newUid;
}
