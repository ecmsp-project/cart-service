package com.ecmsp.cartservice.controller;

import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.dto.ReservationProductMessage;
import com.ecmsp.cartservice.dto.order.OrderCreate;
import com.ecmsp.cartservice.dto.reservation.ReservationResponse;
import com.ecmsp.cartservice.service.CartService;
import com.ecmsp.cartservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final ReservationService reservationService;

    @Autowired
    public CartController(CartService cartService, ReservationService reservationService) {
        this.cartService = cartService;
        this.reservationService = reservationService;
    }


    //TODO get cart by jwt token
    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getCartByUser(@PathVariable("id") Long id) {
        Optional<CartDto> cartData = cartService.getCartById(new UserId(id));
        return cartData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public CartDto addProduct(@RequestBody CartProductDto cartProductDto) {
        return cartService.addProductToCart(new UserId(1), cartProductDto);
    }

    @PostMapping("/create/order")
    public ReservationResponse createOrder() {
        return reservationService.createReservation(new UserId(1));
    }

    @PostMapping("/delete/product")
    public CartDto deleteProductFromDto(@RequestBody CartProductDto cartProductDto) {
        return cartService.deleteProductFromCart(new UserId(1), cartProductDto);
    }


    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteCart() {
        try {
            cartService.deleteCart(new UserId(1L));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update/quantities")
    public CartDto updateQuantitiesOfExistingProducts(@RequestBody CartDto cartDto){
        return cartService.updateQuantitiesOfExistedProducts(new UserId(1), cartDto);
    }
}
