package com.ecmsp.cartservice.kafka.events;

public record KafkaVariantImageUpdatedEvent(
   String variantId,
   String imageUrl
) {}
