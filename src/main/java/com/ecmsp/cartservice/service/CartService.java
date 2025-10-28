package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.CartProduct;
import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    public Optional<Cart> getCartById(UserId userId) {
        return cartRepository.findByUserId(userId.getUserId()).stream().findFirst();
    }

    @Transactional
    public CartDto addProductToCart(UserId userId, CartProductDto productToAdd) {
        Cart cart = getCartOrCreateNew(userId);
        cart.addProduct(convertCartProductToEntity(productToAdd));
        return convertCartToDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartDto deleteProductFromCart(UserId userId, CartProductDto productToDelete) {
        Cart cart = getCartOrCreateNew(userId);
        cart.removeProduct(productToDelete.getProductId());
        return convertCartToDTO(cartRepository.save(cart));
    }


    @Transactional
    public CartDto updateCart(UserId userId, CartDto cartWithNewQuantities) {
        Cart cart = getCartOrCreateNew(userId);
        Set<CartProduct> cartProducts = cartWithNewQuantities.getCartProducts().stream().map(this::convertCartProductToEntity).collect(Collectors.toSet());
        cart.replaceProducts(cartProducts);
        return convertCartToDTO(cartRepository.save(cart));
    }

    public void deleteCart(UserId id) {
        cartRepository.deleteCartByUserId(id.getUserId());
    }

    // Convert Entity to DTO
    public CartDto convertCartToDTO(Cart cart) {
        Set<CartProductDto> cartProductDtos = new HashSet<>();

        if (cart.getCartProducts() != null) {
            cartProductDtos = cart.getCartProducts().stream()
                    .map(this::convertCartToDTO)
                    .collect(Collectors.toSet());
        }

        return CartDto.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .createdAt(cart.getCreatedAt())
                .cartProducts(cartProductDtos)
                .build();
    }

    public Cart getCartOrCreateNew(UserId userId) {
        Optional<Cart> potentialCart = getCartById(userId);
        return potentialCart.orElseGet(() ->cartRepository.save(new Cart(userId.getUserId())));
    }

    // Convert DTO to Entity
    public Cart convertCartToEntity(CartDto cartDTO) {
        Cart cart = new Cart();
        cart.setCartId(cartDTO.getCartId());
        cart.setUserId(cartDTO.getUserId());
        cart.setCreatedAt(cartDTO.getCreatedAt());
        return cart;
    }

    // Convert CartProduct to CartProductDTO
    public CartProductDto convertCartToDTO(CartProduct cartProduct) {
        return CartProductDto.builder()
                .cartId(cartProduct.getCart().getCartId())
                .productId(cartProduct.getProductId())
                .quantity(cartProduct.getQuantity())
                .build();
    }

    public CartProduct convertCartProductToEntity(CartProductDto cartProductDto) {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductId(cartProductDto.getProductId());
        cartProduct.setQuantity(cartProductDto.getQuantity());

        return cartProduct;
    }
}