package com.ecmsp.cartservice.grpc;

import com.ecmsp.cart.v1.*;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.CartProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartGrpcMapper {

    public GetCartResponse toGetCartResponse(CartDto cartDto) {
        List<CartProduct> cartProducts = cartDto.getCartProducts().stream()
                .map(this::toCartProduct)
                .toList();

        return GetCartResponse.newBuilder()
                .setCart(Cart.newBuilder().addAllCartProducts(cartProducts)
                .setCartId(cartDto.getCartId().intValue()).build())
                .build();
    }

    public AddProductResponse toAddProductResponse(CartDto cartDto) {
        List<CartProduct> cartProducts = cartDto.getCartProducts().stream()
                .map(this::toCartProduct)
                .toList();

        return AddProductResponse.newBuilder()
                .setCart(Cart.newBuilder().addAllCartProducts(cartProducts)
                .setCartId(cartDto.getCartId().intValue()).build())
                .build();
    }

    public DeleteProductResponse toDeleteProductResponse(CartDto cartDto) {
        List<CartProduct> cartProducts = cartDto.getCartProducts().stream()
                .map(this::toCartProduct)
                .toList();

        return DeleteProductResponse.newBuilder()
                .setCart(
                        Cart.newBuilder().addAllCartProducts(cartProducts)
                        .setCartId(cartDto.getCartId().intValue()).build()
                ).build();
    }

    public DeleteCartResponse toDeleteCartResponse() {
        return DeleteCartResponse.newBuilder()
                .setSuccess(true)
                .build();
    }

    public UpdateQuantityResponse toUpdateQuantityResponse(CartDto cartDto) {
        List<CartProduct> cartProducts = cartDto.getCartProducts().stream()
                .map(this::toCartProduct)
                .toList();

        return UpdateQuantityResponse.newBuilder()
                .setCart(
                        Cart.newBuilder().setCartId(cartDto.getCartId().intValue()).addAllCartProducts(cartProducts))
                .build();
    }

    public SubtractProductResponse toSubtractProductResponse(CartDto cartDto){
        List<CartProduct> cartProducts = cartDto.getCartProducts().stream()
                .map(this::toCartProduct)
                .toList();

        return SubtractProductResponse.newBuilder()
                .setCat(
                        Cart.newBuilder().setCartId(cartDto.getCartId().intValue()).addAllCartProducts(cartProducts))
                .build();
    }

    public CreateOrderResponse toCreateOrderResponse(String orderId) {
        return CreateOrderResponse.newBuilder()
                .build();
    }

    public CartProductDto toCartProductDto(ProductRequest request) {
        return CartProductDto.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();
    }

    public CartProductDto toCartProductDto(DeleteProductRequest request) {
        return CartProductDto.builder()
                .productId(request.getProductId())
                .build();
    }

    public CartProductDto toCartProductDto(SubtractProductRequest request) {
        return CartProductDto.builder()
                .productId(request.getProduct().getProductId())
                .quantity(request.getProduct().getQuantity())
                .build();
    }

    public CartProductDto toCartProductDto(UpdateQuantityRequest request) {
        return CartProductDto.builder()
                .productId(request.getProduct().getProductId())
                .quantity(request.getProduct().getQuantity())
                .build();
    }

    private CartProduct toCartProduct(CartProductDto productDto) {
        return CartProduct.newBuilder()
                .setProductId(productDto.getProductId())
                .setQuantity(productDto.getQuantity())
                .build();
    }

    private CartProductDto toCartProductDto(CartProduct cartProduct) {
        return CartProductDto.builder()
                .productId(cartProduct.getProductId())
                .quantity(cartProduct.getQuantity())
                .build();
    }
}
