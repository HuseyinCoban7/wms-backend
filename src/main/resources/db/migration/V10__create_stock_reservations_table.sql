CREATE TABLE stock_reservations (
                                    id BIGSERIAL PRIMARY KEY,
                                    order_id BIGINT NOT NULL,
                                    inventory_id BIGINT NOT NULL,
                                    quantity INTEGER NOT NULL,
                                    reserved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    released BOOLEAN NOT NULL DEFAULT FALSE,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP,
                                    created_by VARCHAR(255),
                                    last_modified_by VARCHAR(255),
                                    deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                    CONSTRAINT fk_stock_reservations_order FOREIGN KEY (order_id) REFERENCES orders(id),
                                    CONSTRAINT fk_stock_reservations_inventory FOREIGN KEY (inventory_id) REFERENCES inventory(id)
);

CREATE INDEX idx_stock_reservations_order ON stock_reservations(order_id);
CREATE INDEX idx_stock_reservations_inventory ON stock_reservations(inventory_id);