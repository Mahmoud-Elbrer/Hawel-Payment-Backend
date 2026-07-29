package com.hawel.identity_service.event.impl;


import com.hawel.identity_service.event.*;
import com.hawel.identity_service.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements EventPublisher {


    private final KafkaTemplate<String,Object> kafkaTemplate;



    @Override
    public void publish(Object event) {


        String topic = resolveTopic(event);


        kafkaTemplate.send(topic, event);


        log.info("Kafka event published. topic={}, event={}", topic, event.getClass().getSimpleName());

    }



    private String resolveTopic(Object event){


        if(event instanceof UserRegisteredEvent)
            return KafkaTopics.USER_REGISTERED;


        if(event instanceof UserLoggedInEvent)
            return KafkaTopics.USER_LOGGED_IN;


        if(event instanceof UserLoggedOutEvent)
            return KafkaTopics.USER_LOGGED_OUT;


        if(event instanceof PasswordChangedEvent)
            return KafkaTopics.PASSWORD_CHANGED;

        if(event instanceof TokenRefreshedEvent)
            return KafkaTopics.TOKEN_REFRESHED;

        if(event instanceof OtpSentEvent)
            return KafkaTopics.OTP_SENT;


        throw new IllegalArgumentException(
                "Unknown event type"
        );
    }

}