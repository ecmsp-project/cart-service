package com.ecmsp.cartservice.dto.reservation;

import java.util.List;

public abstract class ReservationResponse {
    private final boolean success;
    private final String message;
    private final List<String> reservedVariantIds;

    protected ReservationResponse(boolean success, String message, List<String> reservedVariantIds) {
        this.success = success;
        this.message = message;
        this.reservedVariantIds = reservedVariantIds;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getReservedVariantIds() {
        return reservedVariantIds;
    }
}
