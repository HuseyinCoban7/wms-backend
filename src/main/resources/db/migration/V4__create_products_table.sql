CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          barcode VARCHAR(100) UNIQUE,
                          name VARCHAR(200) NOT NULL,
                          description VARCHAR(1000),
                          unit VARCHAR(20) NOT NULL,
                          unit_price DECIMAL(10, 2) NOT NULL,
                          min_stock_level INTEGER NOT NULL DEFAULT 0,
                          category VARCHAR(100),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,
                          created_by VARCHAR(255),
                          last_modified_by VARCHAR(255),
                          deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_barcode ON products(barcode);
CREATE INDEX idx_products_name ON products(name);