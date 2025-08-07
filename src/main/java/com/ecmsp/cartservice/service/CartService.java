package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.CartProduct;
import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.dto.ReservationMessageResponse;
import com.ecmsp.cartservice.kafka.OrderKafkaProducer;
import com.ecmsp.cartservice.repository.CartRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final OrderKafkaProducer orderKafkaProducer;

    @Autowired
    public CartService(CartRepository cartRepository, OrderKafkaProducer orderKafkaProducer) {
        this.cartRepository = cartRepository;
        this.orderKafkaProducer = orderKafkaProducer;
    }


    public Optional<CartDto> getCartById(UserId userId) {
        return cartRepository.findById(userId.getUserId())
                .map(this::convertToDTO);
    }

    @Transactional
    public CartDto addProductToCart(UserId userId, CartProductDto productToAdd) {
        CartDto cartDto = getCartOrCreateNew(userId);
        Set<CartProductDto> cartProducts = cartDto.getCartProducts();

        if(cartProducts.stream().map(CartProductDto::getProductId).anyMatch(id -> id.equals(productToAdd.getProductId()))){
            cartProducts.stream().filter(product -> product.getProductId().equals(productToAdd.getProductId()))
                    .map(product -> {
                        product.setQuantity(product.getQuantity() + productToAdd.getQuantity());
                        return product;
                    }).collect(Collectors.toSet());
        } else{
            cartProducts.add(productToAdd);
        }
        cartDto.setCartProducts(cartProducts);

        return convertToDTO(cartRepository.save(convertToEntity(cartDto)));
    }

    @Transactional
    public CartDto deleteProductFromCart(UserId userId, CartProductDto productToDelete) {
        CartDto cart = getCartOrCreateNew(userId);
        Set<CartProductDto> products = cart.getCartProducts();

        if (products.isEmpty())
            return cart;

        cart.setCartProducts(products.stream()
                .filter(product -> product.getProductId().equals(productToDelete.getProductId()))
                .map(p -> {
                    if (p.getQuantity() <= 0)
                        return null;
                    p.setQuantity(p.getQuantity() - productToDelete.getQuantity());
                    return p;
                }).collect(Collectors.toSet()));

        return convertToDTO(cartRepository.save(convertToEntity(cart)));
    }


    @Transactional
    public CartDto updateQuantitiesOfExistedProducts(UserId userId, CartDto cartWithNewQuantities) {
        CartDto cart = getCartOrCreateNew(userId);
        Set<CartProductDto> cartProducts = cart.getCartProducts();

        cartWithNewQuantities.getCartProducts().forEach(updatedProduct -> {
            Optional<CartProductDto> productToDelete = cartProducts.stream().filter(p -> p.getProductId().equals(updatedProduct.getProductId())).findFirst();
            if(productToDelete.isPresent()){
                CartProductDto product = productToDelete.get();
                cartProducts.remove(product);
                product.setQuantity(updatedProduct.getQuantity());
                cartProducts.add(product);
            }
        });

        return convertToDTO(cartRepository.save(convertToEntity(cart)));
    }

    public void deleteCart(UserId id) {
        cartRepository.deleteCartByUserId(id.getUserId());
    }

    // Convert Entity to DTO
    public CartDto convertToDTO(Cart cart) {
        Set<CartProductDto> cartProductDtos = new HashSet<>();

        if (cart.getCartProducts() != null) {
            cartProductDtos = cart.getCartProducts().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toSet());
        }

        return CartDto.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .createdAt(cart.getCreatedAt())
                .cartProducts(cartProductDtos)
                .build();
    }

    public CartDto getCartOrCreateNew(UserId userId) {
        Optional<CartDto> potentialCart = getCartById(userId);
        return potentialCart.orElseGet(CartDto::new);
    }

    // Convert DTO to Entity
    public Cart convertToEntity(CartDto cartDTO) {
        Cart cart = new Cart();
        cart.setCartId(cartDTO.getCartId());
        cart.setUserId(cartDTO.getUserId());
        cart.setCreatedAt(cartDTO.getCreatedAt());
        return cart;
    }

    // Convert CartProduct to CartProductDTO
    private CartProductDto convertToDTO(CartProduct cartProduct) {
        return CartProductDto.builder()
                .cartId(cartProduct.getCart().getCartId())
                .productId(cartProduct.getProductId())
                .quantity(cartProduct.getQuantity())
                .build();
    }
}