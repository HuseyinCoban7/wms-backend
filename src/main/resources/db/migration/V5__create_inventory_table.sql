CREATE TABLE inventory (
                           id BIGSERIAL PRIMARY KEY,
                           product_id BIGINT NOT NULL,
                           location_id BIGINT NOT NULL,
                           quantity INTEGER NOT NULL DEFAULT 0,
                           reserved_quantity INTEGER NOT NULL DEFAULT 0,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP,
                           created_by VARCHAR(255),
                           last_modified_by VARCHAR(255),
                           deleted BOOLEAN NOT NULL DEFAULT FALSE,
                           CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id),
                           CONSTRAINT fk_inventory_location FOREIGN KEY (location_id) REFERENCES locations(id),
                           CONSTRAINT uk_inventory_product_location UNIQUE (product_id, location_id),
                           CONSTRAINT chk_inventory_quantity CHECK (quantity >= 0),
                           CONSTRAINT chk_inventory_reserved CHECK (reserved_quantity >= 0),
                           CONSTRAINT chk_inventory_reserved_lte_quantity CHECK (reserved_quantity <= quantity)
);

CREATE INDEX idx_inventory_product ON inventory(product_id);
CREATE INDEX idx_inventory_location ON inventory(location_id);