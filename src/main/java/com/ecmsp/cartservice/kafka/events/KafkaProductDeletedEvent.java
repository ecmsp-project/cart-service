package com.ecmsp.cartservice.kafka.events;

import java.util.List;

public record KafkaProductDeletedEvent(
        String productId,
        List<String> variantIds
) {}
