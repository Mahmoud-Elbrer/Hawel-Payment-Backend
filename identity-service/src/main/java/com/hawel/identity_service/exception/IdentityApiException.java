package com.hawel.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class IdentityApiException extends RuntimeException{
    private HttpStatus status ;
    private String message  ;

    public IdentityApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
