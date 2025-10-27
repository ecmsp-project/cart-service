package com.ecmsp.cartservice.controller;

import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping()
    public CartDto addProduct(@RequestBody CartProductDto cartProductDto) {
        return cartService.addProductToCart(new UserId(UUID.randomUUID()), cartProductDto);
    }

    @PostMapping("/delete/product")
    public CartDto deleteProductFromDto(@RequestBody CartProductDto cartProductDto) {
        return cartService.deleteProductFromCart(new UserId(UUID.randomUUID()), cartProductDto);
    }


    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteCart() {
        try {
            cartService.deleteCart(new UserId(UUID.randomUUID()));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update/quantities")
    public CartDto updateQuantitiesOfExistingProducts(@RequestBody CartDto cartDto){
        return cartService.updateQuantitiesOfExistedProducts(new UserId(UUID.randomUUID()), cartDto);
    }
}
