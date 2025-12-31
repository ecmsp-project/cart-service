package com.ecmsp.cartservice.repository;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.CartProduct;
import com.ecmsp.cartservice.domain.CartProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {
    
    /**
     * Find all cart products by cart
     * @param cart the cart
     * @return list of cart products in the cart
     */
    List<CartProduct> findByCart(Cart cart);
    
    /**
     * Find all cart products by product ID
     * @param productId the ID of the product
     * @return list of cart products containing the product
     */
    List<CartProduct> findByProductId(UUID productId);
}