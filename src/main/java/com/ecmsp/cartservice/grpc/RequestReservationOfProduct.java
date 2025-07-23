package com.ecmsp.cartservice.grpc;

import com.ecmsp.cartservice.dto.ReservationMessageResponse;
import com.ecmsp.cartservice.dto.ReservationProductMessage;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RequestReservationOfProduct {

    public ReservationMessageResponse reserveProducts(ReservationProductMessage message){
        // TODO: Implement actual gRPC call to product-service
        // For now, simulating a successful reservation
        System.out.println("Attempting to reserve products: " + message.products());

        // Simulate successful reservation with mock variants
        List<String> mockVariants = message.products().stream()
                .map(product -> "variant-" + product.productId() + "-" + product.quantity())
                .toList();

        return new ReservationMessageResponse(true, mockVariants);
    }
}
