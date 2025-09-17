package com.ecmsp.cartservice.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservationEventPayload(
        UUID reservationId,
        UUID clientId,
        LocalDateTime createdAt,
        List<ProductReservation> products,
        BigDecimal totalAmount,
        String status
) {

    public record ProductReservation(
            Long productId,
            UUID productUuid,
            String productName,
            String description,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal totalPrice,
            boolean isReturnable
    ) {
        public ProductReservation {
            if (totalPrice == null && unitPrice != null) {
                totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            }
        }
    }

    public static ReservationEventPayload success(UUID reservationId, UUID clientId, List<ProductReservation> products) {
        BigDecimal totalAmount = products.stream()
                .map(ProductReservation::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReservationEventPayload(
                reservationId,
                clientId,
                LocalDateTime.now(),
                products,
                totalAmount,
                "SUCCESS"
        );
    }

    public static ReservationEventPayload failed(UUID reservationId, UUID clientId, String reason) {
        return new ReservationEventPayload(
                reservationId,
                clientId,
                LocalDateTime.now(),
                List.of(),
                BigDecimal.ZERO,
                "FAILED: " + reason
        );
    }
}