package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.ProductRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimpleCartServiceTest {

    @Autowired
    private CartService cartService;

    private static final UserId TEST_USER_ID = new UserId(1L);

    @Test
    void shouldCreateNewCartForUser() {
        // When
        CartDto result = cartService.getCartOrCreateNew(TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID.getUserId());
        assertThat(result.getCartProducts()).isEmpty();

        System.out.println("✅ Test passed: Created new cart for user");
    }

    @Test
    void shouldAddProductToNewCart() {
        // Given
        ProductRequestDto productRequest = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();

        // When
        CartDto result = cartService.addProductToCart(TEST_USER_ID, productRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID.getUserId());
        assertThat(result.getCartProducts()).hasSize(1);
        assertThat(result.getCartProducts().iterator().next().getProductId()).isEqualTo(101);
        assertThat(result.getCartProducts().iterator().next().getQuantity()).isEqualTo(2);

        System.out.println("✅ Test passed: Added product to new cart");
    }
}