package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.*;
import com.ecmsp.cartservice.dto.OrderCreateMessage;
import com.ecmsp.cartservice.dto.event.CartCreatedEvent;
import com.ecmsp.cartservice.dto.reservation.ReservationResponse;
import com.ecmsp.cartservice.dto.reservation.ReservationSuccessResponse;
import com.ecmsp.cartservice.dto.reservation.ReservationFailedResponse;
import com.ecmsp.cartservice.grpc.RequestReservationOfProduct;
import com.ecmsp.cartservice.kafka.OrderKafkaProducer;
import com.sun.jdi.request.InvalidRequestStateException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

@Service
public class ReservationService {
    private final CartService cartService;
    private final OrderKafkaProducer orderKafkaProducer;
    private final RequestReservationOfProduct requestReservationOfProduct;

    public ReservationService(CartService cartService, OrderKafkaProducer orderKafkaProducer, RequestReservationOfProduct requestReservationOfProduct) {
        this.cartService = cartService;
        this.orderKafkaProducer = orderKafkaProducer;
        this.requestReservationOfProduct = requestReservationOfProduct;
    }

    public ReservationResponse createReservation(UserId userId){
        CartDto userCart = cartService.getCartOrCreateNew(userId);
        Set<CartProductDto> productDtos = userCart.getCartProducts();
        if(productDtos.isEmpty()){
            throw new InvalidRequestStateException("can not create reservation of empty cart");
        }
        ReservationProductMessage reservationProductMessage = buildReservationProductMessage(productDtos);

        ReservationMessageResponse reservationMessageResponse = requestReservationOfProduct.reserveProducts(reservationProductMessage);
        if(reservationMessageResponse.success()){
            CartCreatedEvent cartCreatedEvent = buildCartCreatedEvent(userId, productDtos);
            OrderCreateMessage orderCreateMessage = new OrderCreateMessage(cartCreatedEvent);
            orderKafkaProducer.sendToCreateRawOrder(orderCreateMessage);

            return new ReservationSuccessResponse();
        }

        return new ReservationFailedResponse();
    }
    public ReservationProductMessage buildReservationProductMessage(Set<CartProductDto> productDtos){
        List<ReservationProductMessage.ReservationProduct> products = productDtos.stream()
                .map(dto -> new ReservationProductMessage.ReservationProduct(
                        dto.getProductId(),
                        dto.getQuantity()
                ))
                .collect(Collectors.toList());

        return new ReservationProductMessage(products);
    }

    private CartCreatedEvent buildCartCreatedEvent(UserId userId, Set<CartProductDto> productDtos) {
        UUID clientId = UUID.randomUUID(); // TODO: get actual client UUID from userId

        List<CartCreatedEvent.CartItem> items = productDtos.stream()
                .map(dto -> new CartCreatedEvent.CartItem(
                        UUID.randomUUID(), // TODO: get actual product UUID from productId
                        "Product " + dto.getProductId(), // TODO: get actual product name from product service
                        BigDecimal.valueOf(99.99), // TODO: get actual price from product service
                        dto.getQuantity(),
                        "Product description", // TODO: get actual description from product service
                        true // TODO: get actual returnability from product service
                ))
                .collect(Collectors.toList());

        return new CartCreatedEvent(clientId, items);
    }
}
