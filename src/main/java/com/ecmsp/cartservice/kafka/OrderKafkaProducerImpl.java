package com.ecmsp.cartservice.kafka;

import com.ecmsp.cartservice.dto.OrderCreateMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderKafkaProducerImpl implements OrderKafkaProducer{
    @Override
    public void sendToCreateRawOrder(OrderCreateMessage orderCreateMessage) {
        // TODO: Implement Kafka producer with outbox pattern
        // For now, just logging the message
        System.out.println("Sending order create message: " + orderCreateMessage);
    }
}
