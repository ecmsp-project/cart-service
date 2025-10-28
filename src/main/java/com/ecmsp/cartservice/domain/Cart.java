package com.ecmsp.cartservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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


    public void removeProduct(Integer productId) {
        cartProducts.removeIf(product ->
                product.getProductId().equals(productId)
        );
    }
}
