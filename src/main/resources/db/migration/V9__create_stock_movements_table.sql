CREATE TABLE stock_movements (
                                 id BIGSERIAL PRIMARY KEY,
                                 type VARCHAR(20) NOT NULL,
                                 product_id BIGINT NOT NULL,
                                 from_location_id BIGINT,
                                 to_location_id BIGINT,
                                 quantity INTEGER NOT NULL,
                                 reason VARCHAR(500),
                                 movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 reference_number VARCHAR(100),
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP,
                                 created_by VARCHAR(255),
                                 last_modified_by VARCHAR(255),
                                 deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                 CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products(id),
                                 CONSTRAINT fk_stock_movements_from_location FOREIGN KEY (from_location_id) REFERENCES locations(id),
                                 CONSTRAINT fk_stock_movements_to_location FOREIGN KEY (to_location_id) REFERENCES locations(id)
);

CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_type ON stock_movements(type);
CREATE INDEX idx_stock_movements_date ON stock_movements(movement_date);