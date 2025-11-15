package com.ecmsp.cartservice.kafka.events;

public record KafkaVariantDeletedEvent(
   String variantId,
   String productId
)  {}
