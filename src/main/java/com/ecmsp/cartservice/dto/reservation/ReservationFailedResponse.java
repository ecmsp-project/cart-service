package com.ecmsp.cartservice.dto.reservation;

import java.util.List;

public class ReservationFailedResponse extends ReservationResponse {

    public ReservationFailedResponse(String message) {
        super(false, message, List.of());
    }

    public ReservationFailedResponse() {
        super(false, "Product reservation failed. Please try again or contact support.", List.of());
    }
}
