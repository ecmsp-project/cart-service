package com.ecmsp.cartservice.kafka;

import com.ecmsp.cartservice.kafka.events.KafkaProductDeletedEvent;
import com.ecmsp.cartservice.kafka.events.KafkaVariantDeletedEvent;
import com.ecmsp.cartservice.kafka.events.KafkaVariantImageUpdatedEvent;
import com.ecmsp.cartservice.kafka.events.KafkaVariantPriceChangedEvent;
import com.ecmsp.cartservice.kafka.events.KafkaVariantStockChangedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProductEventConsumer {

    // TODO: service that handles events logic should be injected here and called in each method
    private final ObjectMapper objectMapper;

    public KafkaProductEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.product-deleted}")
    public void consumeProductDeleted(@Payload String productDeletedEventJson) throws JsonProcessingException {

        KafkaProductDeletedEvent productDeletedEvent = objectMapper.readValue(productDeletedEventJson, KafkaProductDeletedEvent.class);
        log.info("Received product deleted event for productId: {}", productDeletedEvent.productId());

        // Handle the product deleted event (e.g., remove product from carts)

    }

    @KafkaListener(topics = "${kafka.topic.variant-price-changed}")
    public void consumeVariantPriceChanged(@Payload String variantPriceChangedEventJson) throws JsonProcessingException {

        KafkaVariantPriceChangedEvent variantPriceChangedEvent = objectMapper.readValue(variantPriceChangedEventJson, KafkaVariantPriceChangedEvent.class);
        log.info("Received variant price changed event for variantId: {}, oldPrice: {}, newPrice: {}",
                variantPriceChangedEvent.variantId(),
                variantPriceChangedEvent.oldPrice(),
                variantPriceChangedEvent.newPrice());
        // Handle the variant price changed event (e.g., update price in carts)

    }

    @KafkaListener(topics = "${kafka.topic.variant-stock-changed}")
    public void consumeVariantStockChanged(@Payload String variantStockChangedEventJson) throws JsonProcessingException {

        KafkaVariantStockChangedEvent variantStockChangedEvent = objectMapper.readValue(variantStockChangedEventJson, KafkaVariantStockChangedEvent.class);
        log.info("Received variant stock changed event for variantId: {}, stockQuantity: {}, isAvailable: {}",
                variantStockChangedEvent.variantId(),
                variantStockChangedEvent.stockQuantity(),
                variantStockChangedEvent.isAvailable());
        // Handle the variant stock changed event (e.g., validate cart items availability)

    }

    @KafkaListener(topics = "${kafka.topic.variant-deleted}")
    public void consumeVariantDeleted(@Payload String variantDeletedEventJson) throws JsonProcessingException {

        KafkaVariantDeletedEvent variantDeletedEvent = objectMapper.readValue(variantDeletedEventJson, KafkaVariantDeletedEvent.class);
        log.info("Received variant deleted event for variantId: {}, productId: {}",
                variantDeletedEvent.variantId(),
                variantDeletedEvent.productId());
        // Handle the variant deleted event (e.g., remove variant from carts)

    }

    @KafkaListener(topics = "${kafka.topic.variant-image-updated}")
    public void consumeVariantImageUpdated(@Payload String variantImageUpdatedEventJson) throws JsonProcessingException {

        KafkaVariantImageUpdatedEvent variantImageUpdatedEvent = objectMapper.readValue(variantImageUpdatedEventJson, KafkaVariantImageUpdatedEvent.class);
        log.info("Received variant image updated event for variantId: {}, imageUrl: {}",
                variantImageUpdatedEvent.variantId(),
                variantImageUpdatedEvent.imageUrl());
        // Handle the variant image updated event (e.g., update image URL in cart cache)

    }

}
