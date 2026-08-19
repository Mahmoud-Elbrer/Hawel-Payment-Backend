package com.hawel.ledger_service.event;


public interface WalletEventPublisher {

    void publish(Object event);

}