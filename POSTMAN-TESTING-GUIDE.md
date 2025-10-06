# Cart Service - Postman Testing Guide

## Prerequisites

1. **Start the PostgreSQL database:**
   ```bash
   cd docker/db
   docker-compose up -d
   ```

2. **Start the Cart Service:**
   ```bash
   ./mvnw spring-boot:run
   ```

   The service will be available at: `http://localhost:8085`

## Import Postman Collection

1. Open Postman
2. Click **Import** button
3. Select the file: `Cart-Service-Postman-Collection.json`
4. The collection "Cart Service API Tests" will be imported with 10 test requests

## Testing Flow

The requests are numbered in a logical testing sequence:

### 1. Get Cart (or Create New)
- **Method:** GET `/api/carts`
- **Purpose:** Retrieves user's cart or creates a new empty one
- **Expected:** Empty cart with `cartProducts: []`

### 2. Add Product to Cart
- **Method:** POST `/api/carts`
- **Body:** `{"productId": 101, "quantity": 2}`
- **Purpose:** Adds first product
- **Expected:** Cart with 1 product (ID: 101, Quantity: 2)

### 3. Add Another Product
- **Method:** POST `/api/carts`
- **Body:** `{"productId": 102, "quantity": 1}`
- **Purpose:** Adds second product
- **Expected:** Cart with 2 products

### 4. Increase Quantity (Add Same Product)
- **Method:** POST `/api/carts`
- **Body:** `{"productId": 101, "quantity": 3}`
- **Purpose:** Tests quantity increase for existing product
- **Expected:** Product 101 quantity becomes 5 (2+3)

### 5. Delete Some Quantity from Product
- **Method:** POST `/api/carts/delete/product`
- **Body:** `{"productId": 101, "quantity": 2}`
- **Purpose:** Reduces quantity without removing product
- **Expected:** Product 101 quantity becomes 3 (5-2)

### 6. Delete Product Completely
- **Method:** POST `/api/carts/delete/product`
- **Body:** `{"productId": 102}` *(Only productId needed!)*
- **Purpose:** Removes product completely regardless of quantity
- **Expected:** Product 102 removed from cart

### 7. Update Quantities (Bulk Update)
- **Method:** POST `/api/carts/update/quantities`
- **Body:** Complex CartDto with cartProducts array
- **Purpose:** Tests bulk quantity updates
- **Expected:** Updates existing products and adds new ones

### 8. Create Order (Reserve Products)
- **Method:** POST `/api/carts/create/order`
- **Purpose:** Tests gRPC integration for product reservation
- **Expected:** Reservation response with success status

### 9. Delete Entire Cart
- **Method:** DELETE `/api/carts`
- **Purpose:** Removes entire cart
- **Expected:** HTTP 204 No Content

### 10. Verify Cart Deleted
- **Method:** GET `/api/carts`
- **Purpose:** Confirms cart deletion and new cart creation
- **Expected:** New empty cart

## Expected Response Format

### Successful Cart Response:
```json
{
    "cartId": 1,
    "userId": 1,
    "createdAt": "2025-10-01T17:30:00",
    "cartProducts": [
        {
            "cartId": 1,
            "productId": 101,
            "quantity": 2
        }
    ]
}
```

### Reservation Response:
```json
{
    "success": true,
    "reservedVariants": ["variant-101", "variant-102"]
}
```

## Testing Notes

1. **User Authentication:** The service uses a mock JWT service that always returns `userId = 1`
2. **Database Persistence:** All operations are persisted to PostgreSQL database
3. **Error Handling:** Invalid requests return appropriate HTTP error codes
4. **Idempotency:** Multiple GET requests should return consistent results

## Troubleshooting

- **Connection refused:** Make sure PostgreSQL is running on `localhost:5432`
- **Service not found:** Verify cart service is running on port `8085`
- **Empty responses:** Check database connection and verify tables exist

## Database Verification

You can connect to the database directly to verify operations:
```bash
psql -h localhost -p 5432 -U root -d shopdb
```

Useful queries:
```sql
-- View all carts
SELECT * FROM cart;

-- View all cart products
SELECT * FROM cart_product;

-- View cart with products for user 1
SELECT c.*, cp.product_id, cp.quantity
FROM cart c
LEFT JOIN cart_product cp ON c.cart_id = cp.cart_id
WHERE c.user_id = 1;
```