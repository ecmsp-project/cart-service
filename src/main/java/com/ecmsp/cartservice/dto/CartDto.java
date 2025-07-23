package com.ecmsp.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long cartId;
    private Long userId;
    private LocalDateTime createdAt;
    
    @Builder.Default
    private Set<CartProductDto> cartProducts = new HashSet<>();
}