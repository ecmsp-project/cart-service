package com.ecmsp.cartservice.dto.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartCreatedEvent(
        UUID clientId,
        List<CartItem> items
) {

    public record CartItem(
            UUID itemId,
            String name,
            BigDecimal price,
            int quantity,
            String description,
            boolean isReturnable
    ) {
    }
}