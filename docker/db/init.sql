CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE cart (
                      cart_id SERIAL PRIMARY KEY,
                      user_id UUID NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_product (
                              cart_id INT,
                              product_id UUID,
                              quantity INT DEFAULT 1,
                              PRIMARY KEY (cart_id, product_id),
                              FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE
);

-- INSERT INTO cart (cart_id, user_id) VALUES
--                                         (1, '00000000-0000-0000-0000-000000000101'),
--                                         (2, '00000000-0000-0000-0000-000000000102'),
--                                         (3, '00000000-0000-0000-0000-000000000103');

-- INSERT INTO cart_product (cart_id, product_id, quantity) VALUES
--                                                              (1, 1001, 2),
--                                                              (1, 1002, 1),
--                                                              (2, 1003, 5),
--                                                              (3, 1001, 1),
--                                                              (3, 1004, 3);

