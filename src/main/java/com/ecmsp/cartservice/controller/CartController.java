package com.ecmsp.cartservice.controller;

import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.DeleteProductRequestDto;
import com.ecmsp.cartservice.dto.ProductRequestDto;
import com.ecmsp.cartservice.dto.reservation.ReservationResponse;
import com.ecmsp.cartservice.jwt.JwtService;
import com.ecmsp.cartservice.service.CartService;
import com.ecmsp.cartservice.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtService jwtService;

    @Autowired
    public CartController(CartService cartService, ReservationService reservationService, JwtService jwtService) {
        this.cartService = cartService;
        this.reservationService = reservationService;
        this.jwtService = jwtService;
    }


    @GetMapping
    public ResponseEntity<CartDto> getCartByUser(HttpServletRequest request) {
        UserId userId = jwtService.extractUserIdFromRequest(request);
        CartDto cartData = cartService.getCartOrCreateNew(userId);
        return ResponseEntity.ok(cartData);
    }


    @PostMapping
    public CartDto addProduct(@RequestBody ProductRequestDto productRequest, HttpServletRequest request) {
        UserId userId = jwtService.extractUserIdFromRequest(request);
        return cartService.addProductToCart(userId, productRequest);
    }

    @PostMapping("/create/order")
    public ReservationResponse createOrder(HttpServletRequest request) {
        UserId userId = jwtService.extractUserIdFromRequest(request);
        return reservationService.createReservation(userId);
    }

    @PostMapping("/delete/product")
    public CartDto deleteProductFromDto(@RequestBody DeleteProductRequestDto deleteRequest, HttpServletRequest request) {
        UserId userId = jwtService.extractUserIdFromRequest(request);
        return cartService.deleteProductCompletely(userId, deleteRequest);
    }


    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteCart(HttpServletRequest request) {
        try {
            UserId userId = jwtService.extractUserIdFromRequest(request);
            cartService.deleteCart(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update/quantities")
    public CartDto updateQuantitiesOfExistingProducts(@RequestBody CartDto cartDto, HttpServletRequest request){
        UserId userId = jwtService.extractUserIdFromRequest(request);
        return cartService.updateQuantitiesOfExistedProducts(userId, cartDto);
    }
}
