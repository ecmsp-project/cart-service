package com.ecmsp.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDto {
    private Long cartId;
    private Integer productId;
    private Integer quantity;
}