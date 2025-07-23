package com.ecmsp.cartservice.kafka;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderKafkaProducerImpl implements OrderKafkaProducer{
    @Override
    public Optional<String> sendToCreateRawOrder(String message) {
        return Optional.empty();
    }
}
