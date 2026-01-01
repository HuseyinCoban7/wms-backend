CREATE TABLE purchase_orders (
                                 id BIGSERIAL PRIMARY KEY,
                                 order_number VARCHAR(50) NOT NULL UNIQUE,
                                 supplier_id BIGINT NOT NULL,
                                 warehouse_id BIGINT NOT NULL,
                                 status VARCHAR(20) NOT NULL,
                                 order_date DATE NOT NULL,
                                 expected_delivery_date DATE,
                                 received_date DATE,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP,
                                 created_by VARCHAR(255),
                                 last_modified_by VARCHAR(255),
                                 deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                 CONSTRAINT fk_purchase_orders_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
                                 CONSTRAINT fk_purchase_orders_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE INDEX idx_purchase_orders_number ON purchase_orders(order_number);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);

CREATE TABLE purchase_order_items (
                                      id BIGSERIAL PRIMARY KEY,
                                      purchase_order_id BIGINT NOT NULL,
                                      product_id BIGINT NOT NULL,
                                      location_id BIGINT NOT NULL,
                                      ordered_quantity INTEGER NOT NULL,
                                      received_quantity INTEGER NOT NULL DEFAULT 0,
                                      unit_price DECIMAL(10, 2) NOT NULL,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP,
                                      created_by VARCHAR(255),
                                      last_modified_by VARCHAR(255),
                                      deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                      CONSTRAINT fk_po_items_purchase_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
                                      CONSTRAINT fk_po_items_product FOREIGN KEY (product_id) REFERENCES products(id),
                                      CONSTRAINT fk_po_items_location FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE INDEX idx_po_items_purchase_order ON purchase_order_items(purchase_order_id);