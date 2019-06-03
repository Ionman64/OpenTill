
CREATE TABLE products (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  barcode VARCHAR NOT NULL UNIQUE,
  price INTEGER NOT NULL DEFAULT 1,
  department VARCHAR(36) NOT NULL,
  supplier VARCHAR(36) NOT NULL,
  labelPrinted BOOLEAN NOT NULL DEFAULT false,
  isCase BOOLEAN NOT NULL DEFAULT false,
  updated DATETIME NOT NULL,
  created DATETIME NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT false,
  max_stock INTEGER NOT NULL DEFAULT 0,
  current_stock INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT barcode_unique UNIQUE (barcode)
);

CREATE TABLE suppliers (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  telephone VARCHAR NOT NULL,
  website VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  created DATETIME NOT NULL,
  updated DATETIME NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE users (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  telephone VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  password_hash VARCHAR NOT NULL,
  code VARCHAR NOT NULL,
  created DATETIME NOT NULL,
  updated DATETIME NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT false,
  CONSTRAINT code_unique UNIQUE (code)
);

CREATE TABLE versions (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  release_date DATETIME NOT NULL,
  major INTEGER NOT NULL,
  minor INTEGER NOT NULL,
  CONSTRAINT build_unique UNIQUE (major, minor)
);

CREATE TABLE servers (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  major INTEGER NOT NULL,
  minor INTEGER NOT NULL,
  ip_address VARCHAR(45) NOT NULL,
  CONSTRAINT ip_address_unique UNIQUE (ip_address)
);

CREATE TABLE configurations (
  config_key VARCHAR(100) NOT NULL PRIMARY KEY, 
  config_value VARCHAR(200) NOT NULL
);

CREATE TABLE departments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    short_hand VARCHAR(15) NOT NULL,
    comments varchar(200),
    colour varchar(7) NOT NULL,
    order_num INTEGER NOT NULL,
    created DATETIME NOT NULL,
    updated DATETIME NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT name_unique UNIQUE(name),
    CONSTRAINT short_hand UNIQUE(short_hand)
);