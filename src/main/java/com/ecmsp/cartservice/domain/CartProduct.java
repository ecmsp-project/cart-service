package com.ecmsp.cartservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "cart_product")
@Data
@NoArgsConstructor
@ToString(exclude = "cart")
@EqualsAndHashCode(exclude = "cart")
@AllArgsConstructor
@IdClass(CartProductId.class)
public class CartProduct {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    
    @Id
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "quantity")
    private Integer quantity;

}