package com.ecmsp.cartservice.repository;

import com.ecmsp.cartservice.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    void deleteCartByUserId(Long userId);
}