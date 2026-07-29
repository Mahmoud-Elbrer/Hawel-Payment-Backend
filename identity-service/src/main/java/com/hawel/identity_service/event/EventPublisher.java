package com.hawel.identity_service.event;


public interface EventPublisher {

    void publish(Object event);

}