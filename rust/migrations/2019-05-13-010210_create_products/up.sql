-- Your SQL goes here
CREATE TABLE products (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  barcode VARCHAR NOT NULL UNIQUE,
  price INTEGER NOT NULL DEFAULT 1,
  CONSTRAINT barcode_unique UNIQUE (barcode)
);

CREATE TABLE suppliers (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  telephone VARCHAR NOT NULL,
  website VARCHAR NOT NULL,
  email VARCHAR NOT NULL
);

CREATE TABLE users (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  telephone VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  password_hash VARCHAR NOT NULL,
  code VARCHAR NOT NULL,
  CONSTRAINT code_unique UNIQUE (code)
);

CREATE TABLE versions (
  release_sequence_num INTEGER NOT NULL PRIMARY KEY,
  major INTEGER NOT NULL,
  minor INTEGER NOT NULL,
  installed BOOLEAN NOT NULL,
  release_date DATETIME NOT NULL,
  CONSTRAINT build_unique UNIQUE (major, minor)
);