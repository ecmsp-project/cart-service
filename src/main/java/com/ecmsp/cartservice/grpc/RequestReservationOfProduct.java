package com.ecmsp.cartservice.grpc;

import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.dto.ReservationMessageResponse;
import com.ecmsp.cartservice.dto.ReservationProductMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestReservationOfProduct {
    public ReservationMessageResponse reserveProducts(ReservationProductMessage message){
        return new ReservationMessageResponse(true);
    }
}
