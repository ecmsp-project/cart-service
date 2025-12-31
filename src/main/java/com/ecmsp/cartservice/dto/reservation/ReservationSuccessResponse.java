package com.ecmsp.cartservice.dto.reservation;

import java.util.List;

public class ReservationSuccessResponse extends ReservationResponse {

    public ReservationSuccessResponse(String message, List<String> reservedVariantIds) {
        super(true, message, reservedVariantIds);
    }

    public ReservationSuccessResponse() {
        super(true, "Reservation completed successfully. Cart has been cleared.", List.of());
    }
}
