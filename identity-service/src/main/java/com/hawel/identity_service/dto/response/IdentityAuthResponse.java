package com.hawel.identity_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IdentityAuthResponse<T> {

    private boolean success;

    private String message;

    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> IdentityAuthResponse<T> success(String message) {
        return IdentityAuthResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> IdentityAuthResponse<T> success(String message, T data) {
        return IdentityAuthResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> IdentityAuthResponse<T> error(String message) {
        return IdentityAuthResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

}