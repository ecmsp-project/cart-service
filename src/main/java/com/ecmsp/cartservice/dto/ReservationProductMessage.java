package com.ecmsp.cartservice.dto;

import java.util.List;

public record ReservationProductMessage(
        List<ReservationProduct> products
) {
    public record ReservationProduct(
            Integer productId,
            Integer quantity
    ) {
    }
}
