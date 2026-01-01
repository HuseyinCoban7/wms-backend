CREATE TABLE locations (
                           id BIGSERIAL PRIMARY KEY,
                           code VARCHAR(50) NOT NULL,
                           description VARCHAR(200),
                           aisle VARCHAR(50),
                           rack VARCHAR(50),
                           bin VARCHAR(50),
                           warehouse_id BIGINT NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP,
                           created_by VARCHAR(255),
                           last_modified_by VARCHAR(255),
                           deleted BOOLEAN NOT NULL DEFAULT FALSE,
                           CONSTRAINT fk_locations_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
                           CONSTRAINT uk_locations_warehouse_code UNIQUE (warehouse_id, code)
);

CREATE INDEX idx_locations_warehouse ON locations(warehouse_id);