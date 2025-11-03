package com.ecmsp.cartservice.domain;

import com.ecmsp.cartservice.dto.CartProductDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "cart")
@Data
@ToString(exclude = "cartProducts")
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartProduct> cartProducts = new HashSet<>();


    public Cart(UUID userId) {
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public void addProduct(CartProduct product) {
        Optional<CartProduct> existing = cartProducts.stream()
                .filter(p -> p.getProductId().equals(product.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(
                    existing.get().getQuantity() + product.getQuantity()
            );
        } else {
            cartProducts.add(product);
            product.setCart(this);
        }
    }

    public void replaceProducts(Set<CartProduct> products) {
        this.cartProducts.clear();

        products.forEach(cartProduct -> {
            cartProduct.setCart(this);
            this.cartProducts.add(cartProduct);
        });
    }

    public void replaceSingleProduct(CartProduct product) {
        Optional<CartProduct> cartProductToModify = this.cartProducts.stream().filter(productFromCart -> Objects.equals(product.getProductId(), productFromCart.getProductId())).findFirst();
        if(cartProductToModify.isPresent()){
            cartProductToModify.get().setQuantity(product.getQuantity());
        }

    }


    public void removeProduct(Integer productId) {
        cartProducts.removeIf(product ->
                product.getProductId().equals(productId)
        );
    }

    public void subtractProduct(CartProduct product){
        Optional<CartProduct> cartProductToModify = this.cartProducts.stream().filter(productFromCart -> Objects.equals(product.getProductId(), productFromCart.getProductId())).findFirst();
        if(cartProductToModify.isPresent()){
            CartProduct toModify = cartProductToModify.get();
            int quantityToSet = toModify.getQuantity()-product.getQuantity();
            if(quantityToSet>0){
                toModify.setQuantity(quantityToSet);
            }else{
                removeProduct(toModify.getProductId());
            }
        }
    }
}
