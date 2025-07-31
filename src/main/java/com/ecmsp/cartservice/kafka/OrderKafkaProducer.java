package com.ecmsp.cartservice.kafka;

import com.ecmsp.cartservice.dto.OrderCreateMessage;

public interface OrderKafkaProducer {
    void sendToCreateRawOrder(OrderCreateMessage orderCreateMessage);
}
