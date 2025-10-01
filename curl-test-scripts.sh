#!/bin/bash

# Cart Service - cURL Testing Scripts
# Make sure the service is running on localhost:8085 and PostgreSQL is available

BASE_URL="http://localhost:8085/api/carts"

echo "=== Cart Service API Testing with cURL ==="
echo

# 1. Get or create cart
echo "1. Getting/Creating cart..."
curl -X GET "$BASE_URL" \
  -H "Content-Type: application/json" \
  | jq '.'
echo -e "\n"

# 2. Add first product
echo "2. Adding first product (ID: 101, Quantity: 2)..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"productId": 101, "quantity": 2}' \
  | jq '.'
echo -e "\n"

# 3. Add second product
echo "3. Adding second product (ID: 102, Quantity: 1)..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"productId": 102, "quantity": 1}' \
  | jq '.'
echo -e "\n"

# 4. Increase quantity of first product
echo "4. Increasing quantity of product 101 by 3 (total should be 5)..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"productId": 101, "quantity": 3}' \
  | jq '.'
echo -e "\n"

# 5. Check current cart state
echo "5. Checking current cart state..."
curl -X GET "$BASE_URL" \
  -H "Content-Type: application/json" \
  | jq '.'
echo -e "\n"

# 6. Reduce quantity of product 101
echo "6. Reducing quantity of product 101 by 2 (should become 3)..."
curl -X POST "$BASE_URL/delete/product" \
  -H "Content-Type: application/json" \
  -d '{"productId": 101, "quantity": 2}' \
  | jq '.'
echo -e "\n"

# 7. Remove product 102 completely
echo "7. Removing product 102 completely (only productId needed)..."
curl -X POST "$BASE_URL/delete/product" \
  -H "Content-Type: application/json" \
  -d '{"productId": 102}' \
  | jq '.'
echo -e "\n"

# 8. Add more products for bulk update test
echo "8. Adding product 103 for bulk update test..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"productId": 103, "quantity": 5}' \
  | jq '.'
echo -e "\n"

# 9. Bulk update quantities
echo "9. Bulk updating quantities..."
curl -X POST "$BASE_URL/update/quantities" \
  -H "Content-Type: application/json" \
  -d '{
    "cartProducts": [
      {"productId": 101, "quantity": 1},
      {"productId": 103, "quantity": 4}
    ]
  }' \
  | jq '.'
echo -e "\n"

# 10. Create order (test gRPC)
echo "10. Creating order (testing gRPC integration)..."
curl -X POST "$BASE_URL/create/order" \
  -H "Content-Type: application/json" \
  | jq '.'
echo -e "\n"

# 11. Final cart check
echo "11. Final cart state before deletion..."
curl -X GET "$BASE_URL" \
  -H "Content-Type: application/json" \
  | jq '.'
echo -e "\n"

# 12. Delete entire cart
echo "12. Deleting entire cart..."
curl -X DELETE "$BASE_URL" \
  -H "Content-Type: application/json" \
  -v
echo -e "\n"

# 13. Verify cart deleted
echo "13. Verifying cart deleted (should create new empty cart)..."
curl -X GET "$BASE_URL" \
  -H "Content-Type: application/json" \
  | jq '.'
echo -e "\n"

echo "=== Testing Complete ==="