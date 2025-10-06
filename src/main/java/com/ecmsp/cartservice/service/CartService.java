package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.CartProduct;
import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.dto.DeleteProductRequestDto;
import com.ecmsp.cartservice.dto.ProductRequestDto;
import com.ecmsp.cartservice.kafka.OrderKafkaProducer;
import com.ecmsp.cartservice.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
        List<Cart> carts = cartRepository.findByUserId(userId.getUserId());
        if (carts.isEmpty()) {
            return Optional.empty();
        }
        // Assuming one cart per user, take the first one
        return Optional.of(convertToDTO(carts.get(0)));
    }

    @Transactional
    public CartDto addProductToCart(UserId userId, ProductRequestDto productRequest) {
        CartDto cartDto = getCartOrCreateNew(userId);

        // Convert ProductRequestDto to CartProductDto with the cart ID
        CartProductDto productToAdd = CartProductDto.builder()
                .cartId(cartDto.getCartId())
                .productId(productRequest.getProductId())
                .quantity(productRequest.getQuantity())
                .build();

        return addProductToCart(userId, productToAdd);
    }

    @Transactional
    public CartDto addProductToCart(UserId userId, CartProductDto productToAdd) {
        // Get or create cart entity
        List<Cart> carts = cartRepository.findByUserId(userId.getUserId());
        Cart cart;
        if (carts.isEmpty()) {
            // Create new cart
            cart = new Cart();
            cart.setUserId(userId.getUserId());
            cart.setCreatedAt(java.time.LocalDateTime.now());
            cart = cartRepository.save(cart);
        } else {
            // Use existing cart (assuming one cart per user)
            cart = carts.get(0);
        }

        // Check if product already exists in cart
        Optional<CartProduct> existingProduct = cart.getCartProducts().stream()
                .filter(cp -> cp.getProductId().equals(productToAdd.getProductId()))
                .findFirst();

        if (existingProduct.isPresent()) {
            // Update quantity if product exists
            CartProduct product = existingProduct.get();
            product.setQuantity(product.getQuantity() + productToAdd.getQuantity());
        } else {
            // Add new product to cart
            CartProduct newProduct = new CartProduct();
            newProduct.setCart(cart);
            newProduct.setProductId(productToAdd.getProductId());
            newProduct.setQuantity(productToAdd.getQuantity());
            cart.getCartProducts().add(newProduct);
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Transactional
    public CartDto deleteProductFromCart(UserId userId, ProductRequestDto productRequest) {
        CartDto cartDto = getCartOrCreateNew(userId);

        // Convert ProductRequestDto to CartProductDto with the cart ID
        CartProductDto productToDelete = CartProductDto.builder()
                .cartId(cartDto.getCartId())
                .productId(productRequest.getProductId())
                .quantity(productRequest.getQuantity())
                .build();

        return deleteProductFromCart(userId, productToDelete);
    }

    @Transactional
    public CartDto deleteProductFromCart(UserId userId, CartProductDto productToDelete) {
        // Get cart entity
        List<Cart> carts = cartRepository.findByUserId(userId.getUserId());
        if (carts.isEmpty()) {
            return getCartOrCreateNew(userId);
        }

        Cart cart = carts.get(0);
        if (cart.getCartProducts().isEmpty()) {
            return convertToDTO(cart);
        }

        // Find product to delete
        Optional<CartProduct> productToRemove = cart.getCartProducts().stream()
                .filter(cp -> cp.getProductId().equals(productToDelete.getProductId()))
                .findFirst();

        if (productToRemove.isPresent()) {
            CartProduct product = productToRemove.get();
            int newQuantity = product.getQuantity() - productToDelete.getQuantity();

            if (newQuantity <= 0) {
                // Remove product completely
                cart.getCartProducts().remove(product);
            } else {
                // Update quantity
                product.setQuantity(newQuantity);
            }
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }


    @Transactional
    public CartDto updateQuantitiesOfExistedProducts(UserId userId, CartDto cartWithNewQuantities) {
        // Get the actual cart entity from database
        List<Cart> carts = cartRepository.findByUserId(userId.getUserId());
        final Cart cart;
        if (carts.isEmpty()) {
            // Create new cart if none exists
            Cart newCart = new Cart();
            newCart.setUserId(userId.getUserId());
            newCart.setCreatedAt(java.time.LocalDateTime.now());
            cart = cartRepository.save(newCart);
        } else {
            cart = carts.get(0);
        }

        // Update quantities for each product in the request
        for (CartProductDto updatedProduct : cartWithNewQuantities.getCartProducts()) {
            // Find existing product in cart
            Optional<CartProduct> existingProduct = cart.getCartProducts().stream()
                .filter(cp -> cp.getProductId().equals(updatedProduct.getProductId()))
                .findFirst();

            if (existingProduct.isPresent()) {
                // Update existing product quantity
                existingProduct.get().setQuantity(updatedProduct.getQuantity());
            } else {
                // Add new product to cart
                CartProduct newProduct = new CartProduct();
                newProduct.setCart(cart);
                newProduct.setProductId(updatedProduct.getProductId());
                newProduct.setQuantity(updatedProduct.getQuantity());
                cart.getCartProducts().add(newProduct);
            }
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Transactional
    public CartDto deleteProductCompletely(UserId userId, DeleteProductRequestDto deleteRequest) {
        // Get cart entity
        List<Cart> carts = cartRepository.findByUserId(userId.getUserId());
        if (carts.isEmpty()) {
            return getCartOrCreateNew(userId);
        }

        Cart cart = carts.get(0);
        if (cart.getCartProducts().isEmpty()) {
            return convertToDTO(cart);
        }

        // Find product to delete completely
        Optional<CartProduct> productToRemove = cart.getCartProducts().stream()
                .filter(cp -> cp.getProductId().equals(deleteRequest.getProductId()))
                .findFirst();

        if (productToRemove.isPresent()) {
            // Always remove product completely
            cart.getCartProducts().remove(productToRemove.get());
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Transactional
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

    @Transactional
    public CartDto getCartOrCreateNew(UserId userId) {
        Optional<CartDto> potentialCart = getCartById(userId);
        if (potentialCart.isPresent()) {
            return potentialCart.get();
        } else {
            // Create new cart in database
            Cart newCart = new Cart();
            newCart.setUserId(userId.getUserId());
            newCart.setCreatedAt(java.time.LocalDateTime.now());
            Cart savedCart = cartRepository.save(newCart);
            return convertToDTO(savedCart);
        }
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