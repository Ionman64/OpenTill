-- Your SQL goes here
CREATE TABLE products (
  id VARCHAR NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  barcode TEXT NOT NULL UNIQUE,
  price INTEGER NOT NULL DEFAULT 1,
  CONSTRAINT barcode_unique UNIQUE (barcode)
)