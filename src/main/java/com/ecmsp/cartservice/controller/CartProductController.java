package com.ecmsp.cartservice.controller;

import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.service.CartProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart-products")
public class CartProductController {

    private final CartProductService cartProductService;

    @Autowired
    public CartProductController(CartProductService cartProductService) {
        this.cartProductService = cartProductService;
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartProductDto>> getCartProductsByCartId(@PathVariable Integer cartId) {
        List<CartProductDto> cartProducts = cartProductService.getCartProductsByCartId(cartId);
        return ResponseEntity.ok(cartProducts);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CartProductDto>> getCartProductsByProductId(@PathVariable UUID productId) {
        List<CartProductDto> cartProducts = cartProductService.getCartProductsByProductId(productId);
        return ResponseEntity.ok(cartProducts);
    }

    @PostMapping
    public ResponseEntity<CartProductDto> addProductToCart(@RequestBody CartProductDto cartProductDto) {
        Optional<CartProductDto> addedProduct = cartProductService.addProductToCart(cartProductDto);
        return addedProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<CartProductDto> updateCartProduct(
            @PathVariable Integer cartId,
            @PathVariable UUID productId,
            @RequestBody CartProductDto cartProductDto) {
        Optional<CartProductDto> updatedProduct = cartProductService.updateCartProduct(cartId, productId, cartProductDto);
        return updatedProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<HttpStatus> deleteCartProduct(
            @PathVariable Integer cartId,
            @PathVariable UUID productId) {
        boolean deleted = cartProductService.deleteCartProduct(cartId, productId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}