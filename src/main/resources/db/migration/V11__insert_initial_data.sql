-- Insert default users (password: Admin123!)
INSERT INTO users (full_name, email, password, role, active, created_by) VALUES
    ('Admin User', 'admin@wms.com', '$2a$10$5iAI1tUTN.Zn46our1.MLeB5c8BYG9./oO2zQ4JdWL0rkJ7QzEM3C', 'ROLE_ADMIN', TRUE, 'system'),
    ('Manager User', 'manager@wms.com', '$2a$10$5iAI1tUTN.Zn46our1.MLeB5c8BYG9./oO2zQ4JdWL0rkJ7QzEM3C', 'ROLE_MANAGER', TRUE, 'system'),
    ('Worker User', 'worker@wms.com', '$2a$10$5iAI1tUTN.Zn46our1.MLeB5c8BYG9./oO2zQ4JdWL0rkJ7QzEM3C', 'ROLE_WORKER', TRUE, 'system');

-- Insert default warehouse
INSERT INTO warehouses (code, name, address, city, country, created_by) VALUES
    ('WH-001', 'Main Warehouse', '123 Industrial Ave', 'Istanbul', 'Turkey', 'system');

-- Insert default locations
INSERT INTO locations (code, description, aisle, rack, bin, warehouse_id, created_by) VALUES
                                                                                          ('A-01-01', 'Aisle A, Rack 1, Bin 1', 'A', '01', '01', 1, 'system'),
                                                                                          ('A-01-02', 'Aisle A, Rack 1, Bin 2', 'A', '01', '02', 1, 'system'),
                                                                                          ('B-01-01', 'Aisle B, Rack 1, Bin 1', 'B', '01', '01', 1, 'system');

-- Insert default supplier
INSERT INTO suppliers (code, name, email, phone, address, active, created_by) VALUES
    ('SUP-001', 'ABC Suppliers Inc', 'contact@abcsuppliers.com', '+90-555-1234567', '456 Supply St, Istanbul', TRUE, 'system');

-- Insert sample products
INSERT INTO products (sku, barcode, name, description, unit, unit_price, min_stock_level, category, created_by) VALUES
                                                                                                                    ('PROD-001', '1234567890123', 'Laptop Dell XPS 15', 'High-performance laptop', 'PCS', 1500.00, 10, 'Electronics', 'system'),
                                                                                                                    ('PROD-002', '1234567890124', 'Mouse Logitech MX Master', 'Wireless mouse', 'PCS', 99.99, 50, 'Electronics', 'system'),
                                                                                                                    ('PROD-003', '1234567890125', 'Keyboard Mechanical RGB', 'Gaming keyboard', 'PCS', 149.99, 30, 'Electronics', 'system');