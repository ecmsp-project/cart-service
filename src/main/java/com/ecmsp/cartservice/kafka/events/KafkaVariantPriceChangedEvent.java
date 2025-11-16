package com.ecmsp.cartservice.kafka.events;

import java.math.BigDecimal;

public record KafkaVariantPriceChangedEvent(
   String variantId,
   BigDecimal oldPrice,
   BigDecimal newPrice
) {}
