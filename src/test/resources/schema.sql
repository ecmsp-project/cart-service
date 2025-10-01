-- Schema for H2 test database

CREATE TABLE cart (
    cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_product (
    cart_id BIGINT,
    product_id INT,
    quantity INT DEFAULT 1,
    PRIMARY KEY (cart_id, product_id),
    FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE
);

CREATE TABLE kafka_outbox (
    id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);