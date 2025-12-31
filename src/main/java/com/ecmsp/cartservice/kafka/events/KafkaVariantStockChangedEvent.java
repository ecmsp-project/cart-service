package com.ecmsp.cartservice.kafka.events;

public record KafkaVariantStockChangedEvent(
   String variantId,
   Integer stockQuantity,
   Boolean isAvailable
){}
