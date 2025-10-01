package com.ecmsp.cartservice.config;

import com.ecmsp.cartservice.dto.ReservationMessageResponse;
import com.ecmsp.cartservice.dto.ReservationProductMessage;
import com.ecmsp.cartservice.grpc.RequestReservationOfProduct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.stream.Collectors;

@TestConfiguration
public class CartTestConfiguration {

    @Bean
    @Primary
    public RequestReservationOfProduct mockRequestReservationOfProduct() {
        return new RequestReservationOfProduct() {
            @Override
            public ReservationMessageResponse reserveProducts(ReservationProductMessage message) {
                // Simulate successful reservation for all products
                List<String> reservedVariants = message.products().stream()
                        .map(product -> "variant-" + product.productId())
                        .collect(Collectors.toList());

                return new ReservationMessageResponse(true, reservedVariants);
            }
        };
    }
}