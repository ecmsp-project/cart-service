package com.ecmsp.cartservice.kafka;

import com.ecmsp.cartservice.dto.OrderCreateMessage;


@Deprecated
public interface OrderKafkaProducer {
    void sendToCreateRawOrder(OrderCreateMessage orderCreateMessage);
}
