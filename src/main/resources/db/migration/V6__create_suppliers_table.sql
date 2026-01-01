CREATE TABLE suppliers (
                           id BIGSERIAL PRIMARY KEY,
                           code VARCHAR(100) NOT NULL UNIQUE,
                           name VARCHAR(200) NOT NULL,
                           email VARCHAR(100),
                           phone VARCHAR(20),
                           address VARCHAR(500),
                           active BOOLEAN NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP,
                           created_by VARCHAR(255),
                           last_modified_by VARCHAR(255),
                           deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_suppliers_code ON suppliers(code);