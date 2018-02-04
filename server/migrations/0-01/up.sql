CREATE DATABASE IF NOT EXISTS opentill;
USE opentill;
CREATE TABLE IF NOT EXISTS `opentill_labels` (
  `id` varchar(36) COLLATE utf8_bin NOT NULL,
  `name` varchar(100) COLLATE utf8_bin NOT NULL,
  `json` blob NOT NULL,
  `updated` int(11) NOT NULL,
  `created` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_operators` (
  `id` varchar(36) NOT NULL,
  `code` varchar(8) NOT NULL,
  `passwordHash` varchar(128) NOT NULL,
  `name` varchar(50) NOT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `comments` varchar(100) DEFAULT NULL,
  `created` int(13) NOT NULL DEFAULT '0',
  `updated` int(13) NOT NULL DEFAULT '0',
  `deleted` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_tblcases` (
  `id` varchar(36) NOT NULL,
  `product` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `quantity` int(6) NOT NULL,
  `barcode` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created` int(13) NOT NULL,
  `updated` int(13) NOT NULL,
  `deleted` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Barcode` (`barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_tblcatagories` (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `shorthand` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `comments` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `colour` varchar(7) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `orderNum` int(2) NOT NULL DEFAULT 0,
  `created` int(13) NOT NULL DEFAULT '0',
  `updated` int(13) NOT NULL DEFAULT '0',
  `deleted` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_tblchat` (
  `id` varchar(36) COLLATE utf8_bin NOT NULL,
  `sender` varchar(36) COLLATE utf8_bin DEFAULT NULL,
  `recipient` varchar(36) COLLATE utf8_bin DEFAULT NULL,
  `message` varchar(200) COLLATE utf8_bin NOT NULL,
  `updated` int(13) NOT NULL,
  `created` int(13) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_tblorders_to_products` (
  `orderId` varchar(36) COLLATE utf8_bin NOT NULL,
  `productId` varchar(36) NOT NULL,
  `quantity` int(13) NOT NULL,
  `created` int(13) DEFAULT NULL,
  `updated` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`productId`, `orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_tblorders` (
  `id` varchar(36) COLLATE utf8_bin NOT NULL,
  `supplier` varchar(36) COLLATE utf8_bin NOT NULL,
  `created` int(13) NOT NULL,
  `updated` int(13) NOT NULL,
  `ended` int(13) DEFAULT NULL,
  `deleted` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_tblproducts` (
  `id` varchar(36) COLLATE utf8_bin NOT NULL,
  `name` varchar(100) COLLATE utf8_bin NOT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `barcode` varchar(13) COLLATE utf8_bin NOT NULL,
  `department` varchar(36) COLLATE utf8_bin DEFAULT NULL,
  `labelprinted` int(1) NOT NULL DEFAULT '0',
  `isCase` tinyint(1) NOT NULL DEFAULT '0',
  `units` int(4) NOT NULL DEFAULT '1',
  `unitType` varchar(36) COLLATE utf8_bin NOT NULL,
  `updated` int(13) NOT NULL,
  `created` int(13) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `status` int(1) NOT NULL DEFAULT '1',
  `max_stock` int(4) NOT NULL DEFAULT '0',
  `current_stock` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Barcode` (`barcode`),
  KEY `barcode_2` (`barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_tblsuppliers` (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(35) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `telephone` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `website` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `comments` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `created` int(13) NOT NULL,
  `updated` int(13) NOT NULL,
  `deleted` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `opentill_tblunits` (
  `id` varchar(36) NOT NULL,
  `unit` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `updated` int(13) NOT NULL,
  `created` int(13) NOT NULL,
  `deleted` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_transactions` (
  `id` varchar(36) CHARACTER SET latin1 NOT NULL,
  `started` int(20) DEFAULT '0',
  `ended` int(20) DEFAULT '0',
  `updated` int(20) DEFAULT '0',
  `total` double(20,2) NOT NULL DEFAULT '0.00',
  `cashier` varchar(36) CHARACTER SET latin1 NOT NULL,
  `money_given` double(20,2) NOT NULL DEFAULT '0.00',
  `card` double(20,2) NOT NULL DEFAULT '0.00',
  `cashback` double(20,2) NOT NULL DEFAULT '0.00',
  `payee` varchar(36) CHARACTER SET latin1 DEFAULT NULL,
  `type` varchar(36) CHARACTER SET latin1 NOT NULL DEFAULT 'PURCHASE',
  `json` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_transactiontoproducts` (
  `id` varchar(36) COLLATE utf8_bin NOT NULL,
  `transaction_id` varchar(36) COLLATE utf8_bin NOT NULL,
  `product_id` varchar(36) COLLATE utf8_bin DEFAULT NULL,
  `price` double(20,2) NOT NULL,
  `department` varchar(36) COLLATE utf8_bin DEFAULT NULL,
  `created` int(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `transaction_id` (`transaction_id`),
  KEY `department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `opentill_usertypes` (
  `id` varchar(36) COLLATE utf8_bin NOT NULL,
  `type` varchar(40) COLLATE utf8_bin NOT NULL,
  `updated` int(13) NOT NULL,
  `created` int(13) NOT NULL,
  `deleted` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;