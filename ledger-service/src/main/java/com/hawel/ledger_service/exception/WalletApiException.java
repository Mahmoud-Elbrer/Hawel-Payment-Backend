package com.hawel.ledger_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LedgerException extends RuntimeException{
    private HttpStatus status ;
    private String message  ;

    public LedgerException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
