package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.CartProduct;
import com.ecmsp.cartservice.domain.CartProductId;
import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.repository.CartProductRepository;
import com.ecmsp.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartProductService {

    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CartProductService(CartProductRepository cartProductRepository, CartRepository cartRepository) {
        this.cartProductRepository = cartProductRepository;
        this.cartRepository = cartRepository;
    }

    public List<CartProductDto> getCartProductsByCartId(Integer cartId) {
//        Optional<Cart> cartOptional = cartRepository.findById(cartId);
//
//        if (cartOptional.isPresent()) {
//            return cartProductRepository.findByCart(cartOptional.get()).stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//        }

        return List.of();
    }

    public List<CartProductDto> getCartProductsByProductId(Integer productId) {
        return cartProductRepository.findByProductId(productId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<CartProductDto> addProductToCart(CartProductDto cartProductDTO) {
        Optional<Cart> cartOptional = cartRepository.findById(cartProductDTO.getCartId());
        
        if (cartOptional.isPresent()) {
            CartProduct cartProduct = convertToEntity(cartProductDTO, cartOptional.get());
            CartProduct savedCartProduct = cartProductRepository.save(cartProduct);
            return Optional.of(convertToDto(savedCartProduct));
        }
        
        return Optional.empty();
    }

    public Optional<CartProductDto> updateCartProduct(Integer cartId, Integer productId, CartProductDto cartProductDTO) {
//        Optional<Cart> cartOptional = cartRepository.findById(cartId);
//
//        if (cartOptional.isPresent()) {
//            Cart cart = cartOptional.get();
//            CartProductId id = new CartProductId(cart, productId);
//
//            return cartProductRepository.findById(id)
//                    .map(existingCartProduct -> {
//                        existingCartProduct.setQuantity(cartProductDTO.getQuantity());
//                        return cartProductRepository.save(existingCartProduct);
//                    })
//                    .map(this::convertToDto);
//        }
        
        return Optional.empty();
    }

    public boolean deleteCartProduct(Integer cartId, Integer productId) {
//        Optional<Cart> cartOptional = cartRepository.findById(cartId);
//
//        if (cartOptional.isPresent()) {
//            Cart cart = cartOptional.get();
//            CartProductId id = new CartProductId(cart, productId);
//            cartProductRepository.deleteById(id);
//            return true;
//        }
        
        return false;
    }

    // Convert Entity to DTO
    public CartProductDto convertToDto(CartProduct cartProduct) {
        return CartProductDto.builder()
                .cartId(cartProduct.getCart().getCartId())
                .productId(cartProduct.getProductId())
                .quantity(cartProduct.getQuantity())
                .build();
    }

    // Convert DTO to Entity
    public CartProduct convertToEntity(CartProductDto cartProductDto, Cart cart) {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProductId(cartProductDto.getProductId());
        cartProduct.setQuantity(cartProductDto.getQuantity());
        return cartProduct;
    }
}