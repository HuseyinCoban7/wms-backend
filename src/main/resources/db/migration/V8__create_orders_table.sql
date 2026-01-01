CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(50) NOT NULL UNIQUE,
                        customer_name VARCHAR(200) NOT NULL,
                        shipping_address VARCHAR(500),
                        warehouse_id BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        order_date DATE NOT NULL,
                        shipped_date DATE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,
                        created_by VARCHAR(255),
                        last_modified_by VARCHAR(255),
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        CONSTRAINT fk_orders_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             location_id BIGINT NOT NULL,
                             quantity INTEGER NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP,
                             created_by VARCHAR(255),
                             last_modified_by VARCHAR(255),
                             deleted BOOLEAN NOT NULL DEFAULT FALSE,
                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
                             CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
                             CONSTRAINT fk_order_items_location FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE INDEX idx_order_items_order ON order_items(order_id);