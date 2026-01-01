CREATE TABLE warehouses (
                            id BIGSERIAL PRIMARY KEY,
                            code VARCHAR(100) NOT NULL UNIQUE,
                            name VARCHAR(200) NOT NULL,
                            address VARCHAR(500),
                            city VARCHAR(50),
                            country VARCHAR(50),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP,
                            created_by VARCHAR(255),
                            last_modified_by VARCHAR(255),
                            deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_warehouses_code ON warehouses(code);