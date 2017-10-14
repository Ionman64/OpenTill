<?php
	//Peter Pickerill 10/08/2017
	//Class to enabled basic syncing between two or more live databases
	function get_last_updated() {
		$db = get_pdo_connection();
		$sql = "SELECT value FROM kvs_config WHERE key = 'updated' LIMIT 1";
	}
	$last_updated = get_last_updated();
	//Read 
	$sql = "SELECT * FROM kvs_inventory_levels WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_operators WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_tblcases WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_tblcatagories WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_tblorders WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_tblproducts WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_tblsuppliers WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_tblunits WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_transactions WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_transactiontoproducts WHERE created < ? OR updated < ?";
	$sql = "SELECT * FROM kvs_usertypes WHERE created < ? OR updated < ?";
	//Write
	$sql = "INSERT INTO kvs_inventory_levels (id,product,max,display,reorder,current,created) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE product=?, max=?, display=?, reorder=?, current=?, created=?;";
	$sql = "INSERT INTO kvs_operators (id,code,name,telephone,email,created,updated,deleted) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE code=?, name=?, telephone=?, email=?, created=?, updated=?, deleted=?;";
	$sql = "INSERT INTO kvs_tblcases (id,product,quantity,barcode,created,updated,deleted) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE product=?, quantity=?, barcode=?, created=?, updated=?, deleted=?;";
	$sql = "INSERT INTO kvs_tblcatagories (id,name,shorthand,comments,colour,orderNum,created,updated,deleted) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?, shorthand=?, comments=?, colour=?, orderNum=?, created=?, updated=?, deleted=?;";
	$sql = "INSERT INTO kvs_tblorders (id,name,supplier,created,updated,ended,deleted) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?, supplier=?, created=?, updated=?, ended=?, deleted=?;";
	$sql = "INSERT INTO kvs_tblproducts (id,name,price,barcode,department,labelprinted,updated,created,deleted,status) VALUES (?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?, price=?, barcode=?, department=?, labelprinted=?, updated=?, created=?, deleted=?, status=?;";
	$sql = "INSERT INTO kvs_tblsuppliers (id,name,telephone,email,website,comments,created,updated,deleted) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?, telephone=?, email=?, website=?, comments=?, created=?, updated=?, deleted=?;";
	$sql = "INSERT INTO kvs_tblunits (id,unit,updated,created,deleted) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE unit=?, updated=?, created=?, deleted=?;";
	$sql = "INSERT INTO kvs_transactions (id,started,ended,updated,total,cashier,money_given,card,cashback,payee,type,json) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE started=?, ended=?, updated=?, total=?, cashier=?, money_given=?, card=?, cashback=?, payee=?, type=?, json=?;";
	$sql = "INSERT INTO kvs_transactiontoproducts (id,transaction_id,product_id,price,department,created) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE transaction_id=?, product_id=?, price=?, department=?, created=?;";
	$sql = "INSERT INTO kvs_usertypes (id,type,updated,created,deleted) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE type=?, updated=?, created=?, deleted=?;";

?>
