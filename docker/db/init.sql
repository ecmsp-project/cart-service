CREATE TABLE cart (
                      cart_id SERIAL PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_product (
                              cart_id INT,
                              product_id INT,
                              quantity INT DEFAULT 1,
                              PRIMARY KEY (cart_id, product_id),
                              FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE
);

INSERT INTO cart (cart_id, user_id) VALUES
                                        (1, 101),
                                        (2, 102),
                                        (3, 103);

INSERT INTO cart_product (cart_id, product_id, quantity) VALUES
                                                             (1, 1001, 2),
                                                             (1, 1002, 1),
                                                             (2, 1003, 5),
                                                             (3, 1001, 1),
                                                             (3, 1004, 3);

CREATE TABLE kafka_outbox (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);