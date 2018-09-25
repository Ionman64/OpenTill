USE :database_name:;
ALTER TABLE :prefix:operators ADD COLUMN user_type VARCHAR(36) DEFAULT 1;
ALTER TABLE :prefix:tblproducts ADD COLUMN added_tax DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE :prefix:tblproducts ADD COLUMN contains_product VARCHAR(15);

CREATE TABLE :prefix:tblcases (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    barcode VARCHAR(13) NOT NULL UNIQUE KEY,
    product VARCHAR(36) NOT NULL,
    supplier VARCHAR(36),
    units int NOT NULL DEFAULT 1,
    created int,
    deleted int(1) NOT NULL DEFAULT 0
); 

ALTER TABLE :prefix:tblproducts DROP COLUMN isCase;

CREATE TABLE kvs_forgotPassword (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    time int(13),
    userId VARCHAR(36) NOT NULL,
    token VARCHAR(128) NOT NULL
); 