CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE cart (
                      cart_id SERIAL PRIMARY KEY,
                      user_id UUID NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_product (
                              cart_id INT NOT NULL,
                              product_id UUID NOT NULL,
                              quantity INT DEFAULT 1,

                              PRIMARY KEY (cart_id, product_id),

                              CONSTRAINT fk_cart
                                    FOREIGN KEY (cart_id)
                                    REFERENCES cart(cart_id)
                                    ON DELETE CASCADE
);

INSERT INTO cart (cart_id, user_id) VALUES
                                        (1, '00000000-0000-0000-0000-000000000101'),
                                        (2, '00000000-0000-0000-0000-000000000102'),
                                        (3, '00000000-0000-0000-0000-000000000103');

INSERT INTO cart_product (cart_id, product_id, quantity) VALUES
                                         (1, '11111111-0000-0000-0000-000000001001', 2),
                                         (1, '11111111-0000-0000-0000-000000001002', 1),

                                         (2, '11111111-0000-0000-0000-000000001003', 5),

                                         (3, '11111111-0000-0000-0000-000000001001', 1),
                                         (3, '11111111-0000-0000-0000-000000001004', 3);

CREATE TABLE KAFKA_OUTBOX (
                              id UUID PRIMARY KEY,
                              payload JSONB NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              processed BOOLEAN NOT NULL DEFAULT FALSE,
                              processed_at TIMESTAMP,
                              event_type VARCHAR(255) NOT NULL,
                              topic VARCHAR(255)
);

CREATE INDEX idx_kafka_outbox_unprocessed ON KAFKA_OUTBOX (processed, created_at) WHERE processed = FALSE;
CREATE INDEX idx_kafka_outbox_event_type ON KAFKA_OUTBOX (event_type);
CREATE INDEX idx_kafka_outbox_created_at ON KAFKA_OUTBOX (created_at);