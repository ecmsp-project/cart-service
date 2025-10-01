package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.config.CartTestConfiguration;
import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.CartProduct;
import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.DeleteProductRequestDto;
import com.ecmsp.cartservice.dto.ProductRequestDto;
import com.ecmsp.cartservice.repository.CartRepository;
import com.ecmsp.cartservice.repository.CartProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(CartTestConfiguration.class)
@Transactional
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    private static final UserId TEST_USER_ID = new UserId(1L);
    private static final UserId TEST_USER_ID_2 = new UserId(2L);

    @BeforeEach
    void setUp() {
        // Clear all data before each test
        cartProductRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    void shouldAddNewProductToEmptyCart() {
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

        // Verify in database
        List<Cart> cartsInDb = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        assertThat(cartsInDb).hasSize(1);
        Cart cartInDb = cartsInDb.get(0);
        assertThat(cartInDb.getCartProducts()).hasSize(1);

        CartProduct productInDb = cartInDb.getCartProducts().iterator().next();
        assertThat(productInDb.getProductId()).isEqualTo(101);
        assertThat(productInDb.getQuantity()).isEqualTo(2);

        System.out.println("✅ Test passed: Added new product to empty cart");
    }

    @Test
    void shouldIncreaseQuantityWhenAddingSameProduct() {
        // Given - first add a product
        ProductRequestDto firstAdd = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();
        cartService.addProductToCart(TEST_USER_ID, firstAdd);

        // When - add same product again
        ProductRequestDto secondAdd = ProductRequestDto.builder()
                .productId(101)
                .quantity(3)
                .build();
        CartDto result = cartService.addProductToCart(TEST_USER_ID, secondAdd);

        // Then
        assertThat(result.getCartProducts()).hasSize(1);
        assertThat(result.getCartProducts().iterator().next().getProductId()).isEqualTo(101);
        assertThat(result.getCartProducts().iterator().next().getQuantity()).isEqualTo(5); // 2 + 3

        // Verify in database
        List<Cart> cartsInDb = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        assertThat(cartsInDb).hasSize(1);
        Cart cartInDb = cartsInDb.get(0);
        CartProduct productInDb = cartInDb.getCartProducts().iterator().next();
        assertThat(productInDb.getQuantity()).isEqualTo(5);

        System.out.println("✅ Test passed: Increased quantity when adding same product");
    }

    @Test
    void shouldAddMultipleDifferentProducts() {
        // Given
        ProductRequestDto product1 = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();
        ProductRequestDto product2 = ProductRequestDto.builder()
                .productId(102)
                .quantity(1)
                .build();
        ProductRequestDto product3 = ProductRequestDto.builder()
                .productId(103)
                .quantity(4)
                .build();

        // When
        cartService.addProductToCart(TEST_USER_ID, product1);
        cartService.addProductToCart(TEST_USER_ID, product2);
        CartDto result = cartService.addProductToCart(TEST_USER_ID, product3);

        // Then
        assertThat(result.getCartProducts()).hasSize(3);

        // Verify in database
        List<Cart> cartsInDb = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        assertThat(cartsInDb).hasSize(1);
        Cart cartInDb = cartsInDb.get(0);
        assertThat(cartInDb.getCartProducts()).hasSize(3);

        System.out.println("✅ Test passed: Added multiple different products");
    }

    @Test
    void shouldDecreaseQuantityWhenDeletingPartOfProduct() {
        // Given - add product with quantity 5
        ProductRequestDto addProduct = ProductRequestDto.builder()
                .productId(101)
                .quantity(5)
                .build();
        cartService.addProductToCart(TEST_USER_ID, addProduct);

        // When - delete 2 items
        ProductRequestDto deleteRequest = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();
        CartDto result = cartService.deleteProductFromCart(TEST_USER_ID, deleteRequest);

        // Then
        assertThat(result.getCartProducts()).hasSize(1);
        assertThat(result.getCartProducts().iterator().next().getQuantity()).isEqualTo(3); // 5 - 2

        // Verify in database
        List<Cart> cartsInDb = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        assertThat(cartsInDb).hasSize(1);
        Cart cartInDb = cartsInDb.get(0);
        CartProduct productInDb = cartInDb.getCartProducts().iterator().next();
        assertThat(productInDb.getQuantity()).isEqualTo(3);

        System.out.println("✅ Test passed: Decreased quantity when deleting part of product");
    }

    @Test
    void shouldRemoveProductCompletelyWhenDeletingAllQuantity() {
        // Given - add product
        ProductRequestDto addProduct = ProductRequestDto.builder()
                .productId(101)
                .quantity(3)
                .build();
        cartService.addProductToCart(TEST_USER_ID, addProduct);

        // When - delete product completely (new endpoint)
        DeleteProductRequestDto deleteRequest = DeleteProductRequestDto.builder()
                .productId(101)
                .build();
        CartDto result = cartService.deleteProductCompletely(TEST_USER_ID, deleteRequest);

        // Then
        assertThat(result.getCartProducts()).isEmpty();

        // Verify in database
        List<Cart> cartsInDb = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        if (!cartsInDb.isEmpty()) {
            assertThat(cartsInDb.get(0).getCartProducts()).isEmpty();
        }

        System.out.println("✅ Test passed: Removed product completely when deleting all quantity");
    }

    @Test
    void shouldDeleteProductCompletelyFromMultipleProducts() {
        // Given - add multiple products
        ProductRequestDto product1 = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();
        ProductRequestDto product2 = ProductRequestDto.builder()
                .productId(102)
                .quantity(5)
                .build();
        cartService.addProductToCart(TEST_USER_ID, product1);
        cartService.addProductToCart(TEST_USER_ID, product2);

        // When - delete one product completely
        DeleteProductRequestDto deleteRequest = DeleteProductRequestDto.builder()
                .productId(102)
                .build();
        CartDto result = cartService.deleteProductCompletely(TEST_USER_ID, deleteRequest);

        // Then - only product 101 should remain
        assertThat(result.getCartProducts()).hasSize(1);
        assertThat(result.getCartProducts().iterator().next().getProductId()).isEqualTo(101);
        assertThat(result.getCartProducts().iterator().next().getQuantity()).isEqualTo(2);

        // Verify in database
        List<Cart> cartsInDb = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        assertThat(cartsInDb).hasSize(1);
        Cart cartInDb = cartsInDb.get(0);
        assertThat(cartInDb.getCartProducts()).hasSize(1);
        CartProduct productInDb = cartInDb.getCartProducts().iterator().next();
        assertThat(productInDb.getProductId()).isEqualTo(101);
        assertThat(productInDb.getQuantity()).isEqualTo(2);

        System.out.println("✅ Test passed: Deleted product completely from multiple products");
    }

    @Test
    void shouldCreateNewCartWhenUserHasNoCart() {
        // Given - user has no cart
        Optional<CartDto> existingCart = cartService.getCartById(TEST_USER_ID);
        assertThat(existingCart).isEmpty();

        // When
        CartDto result = cartService.getCartOrCreateNew(TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID.getUserId());
        assertThat(result.getCartProducts()).isEmpty();

        System.out.println("✅ Test passed: Created new cart when user has no cart");
    }

    @Test
    void shouldReturnExistingCartWhenUserHasCart() {
        // Given - user already has a cart with products
        ProductRequestDto product = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();
        CartDto existingCart = cartService.addProductToCart(TEST_USER_ID, product);

        // When
        CartDto result = cartService.getCartOrCreateNew(TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCartId()).isEqualTo(existingCart.getCartId());
        assertThat(result.getCartProducts()).hasSize(1);

        System.out.println("✅ Test passed: Returned existing cart when user has cart");
    }

    @Test
    void shouldDeleteEntireCart() {
        // Given - user has cart with products
        ProductRequestDto product1 = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();
        ProductRequestDto product2 = ProductRequestDto.builder()
                .productId(102)
                .quantity(1)
                .build();

        cartService.addProductToCart(TEST_USER_ID, product1);
        cartService.addProductToCart(TEST_USER_ID, product2);

        // Verify cart exists
        List<Cart> cartBeforeDelete = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        assertThat(cartBeforeDelete).hasSize(1);
        assertThat(cartBeforeDelete.get(0).getCartProducts()).hasSize(2);

        // When
        cartService.deleteCart(TEST_USER_ID);

        // Then
        List<Cart> cartAfterDelete = cartRepository.findByUserId(TEST_USER_ID.getUserId());
        assertThat(cartAfterDelete).isEmpty();

        System.out.println("✅ Test passed: Deleted entire cart");
    }

    @Test
    void shouldIsolateCartsBetweenDifferentUsers() {
        // Given - two different users
        ProductRequestDto product1 = ProductRequestDto.builder()
                .productId(101)
                .quantity(2)
                .build();
        ProductRequestDto product2 = ProductRequestDto.builder()
                .productId(102)
                .quantity(3)
                .build();

        // When - add products to different users' carts
        CartDto user1Cart = cartService.addProductToCart(TEST_USER_ID, product1);
        CartDto user2Cart = cartService.addProductToCart(TEST_USER_ID_2, product2);

        // Then - carts should be separate
        assertThat(user1Cart.getUserId()).isEqualTo(TEST_USER_ID.getUserId());
        assertThat(user1Cart.getCartProducts()).hasSize(1);
        assertThat(user1Cart.getCartProducts().iterator().next().getProductId()).isEqualTo(101);

        assertThat(user2Cart.getUserId()).isEqualTo(TEST_USER_ID_2.getUserId());
        assertThat(user2Cart.getCartProducts()).hasSize(1);
        assertThat(user2Cart.getCartProducts().iterator().next().getProductId()).isEqualTo(102);

        // Verify in database
        assertThat(cartRepository.count()).isEqualTo(2);

        System.out.println("✅ Test passed: Isolated carts between different users");
    }
}