package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.*;
import com.ecmsp.cartservice.dto.OrderCreateMessage;
import com.ecmsp.cartservice.dto.reservation.ReservationResponse;
import com.ecmsp.cartservice.dto.reservation.ReservationSuccessResponse;
import com.ecmsp.cartservice.grpc.RequestReservationOfProduct;
import com.ecmsp.cartservice.kafka.OrderKafkaProducer;
import com.sun.jdi.request.InvalidRequestStateException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
            OrderCreateMessage orderCreateMessage = new OrderCreateMessage(reservationMessageResponse.variants());
            orderKafkaProducer.sendToCreateRawOrder(orderCreateMessage);

            return new ReservationSuccessResponse();
        }

        // TODO: provide proper return statement
        return null;
    }
    public ReservationProductMessage buildReservationProductMessage(Set<CartProductDto> productDtos){
        return new ReservationProductMessage();
    }
}
