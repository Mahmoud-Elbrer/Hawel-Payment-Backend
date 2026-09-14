package com.hawel.payment_service.client;

import com.hawel.payment_service.client.dto.CardValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "card-service", url = "${services.card.url}")
public interface CardClient {

    @GetMapping("/api/v1/cards/uid/{uid}/validate")
    CardValidationResponse validateByUid(@PathVariable("uid") String uid);
}