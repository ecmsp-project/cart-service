package com.ecmsp.cartservice.repository;

import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.dto.CartDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    // TODO: what is it for?
//    Cart saveCart(Cart cart);

    void deleteCartByUserId(Long userId);
}