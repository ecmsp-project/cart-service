package com.ecmsp.cartservice.kafka;

import com.ecmsp.cartservice.dto.OrderCreateMessage;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderKafkaProducerImpl implements OrderKafkaProducer{
    public Optional<String> sendToCreateRawOrder(String message) {
        return Optional.empty();
    }

    @Override
    public void sendToCreateRawOrder(OrderCreateMessage orderCreateMessage) {
        // TODO: implement
    }
}
