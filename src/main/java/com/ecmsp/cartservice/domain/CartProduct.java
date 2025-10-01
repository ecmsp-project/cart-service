package com.ecmsp.cartservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CartProductId.class)
public class CartProduct {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id")
    @EqualsAndHashCode.Exclude
    private Cart cart;
    
    @Id
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "quantity")
    private Integer quantity;

}