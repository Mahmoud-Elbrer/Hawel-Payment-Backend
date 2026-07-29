package com.hawel.wallet_service.event;


public interface WalletEventPublisher {

    void publish(Object event);

}