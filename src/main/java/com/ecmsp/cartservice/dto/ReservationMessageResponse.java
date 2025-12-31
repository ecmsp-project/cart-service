package com.ecmsp.cartservice.dto;

import java.util.List;

public record ReservationMessageResponse(boolean success, List<String> variants) {


}
