USE :database_name:;
ALTER TABLE :prefix:operators ADD COLUMN user_type VARCHAR(36) DEFAULT 1;
ALTER TABLE :prefix:tblproducts ADD COLUMN added_tax DECIMAL(10,2) DEFAULT 0.00;
