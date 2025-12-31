package com.ecmsp.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long cartId;
    private UUID userId;
    private LocalDateTime createdAt;
    
    @Builder.Default
    private Set<CartProductDto> cartProducts = new HashSet<>();
}