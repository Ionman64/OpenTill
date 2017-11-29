<?php
	session_start();
	date_default_timezone_set('Europe/London');
	setlocale(LC_ALL, array('en_GB.UTF8','en_GB@euro','en_GB','english'));
	$function = isset($_GET['function']) ? $_GET['function'] : null;
	if ($function==null) {
		error_out();
	}
	switch ($function) {
		case 'BARCODE':
			barcode();
			break;
		case 'DEPARTMENTS':
			get_departments();
			break;
		case 'TRANSACTION':
			start_transaction();
			break;
		case 'SYNC':
			sync_transaction();
			break;
		case 'GETTRANSACTION':
			get_transaction_products();
			break;
		case 'GETPRODUCTSALES':
			get_product_transactions();
			break;
		case 'GETTRANSACTIONS':
			get_transactions();
			break;
		case 'COMPLETETRANSACTION':
			complete_transaction();
			break;
		case 'CLEARTRANSACTION':
			cancel_transaction();
			break;
		case 'UPDATEPRODUCT':
			update();
			break;
		case 'GETPRODUCT':
			get_product();
			break;
		case 'PRINTLABEL':
			print_label();
			break;
		case 'GETALLSUPPLIERS':
			get_all_suppliers();
			break;
		case 'GETSUPPLIER':
			select_supplier();
			break;
		case 'UPDATESUPPLIER':
			update_supplier();
			break;
		case 'ADDSUPPLIER':
			create_supplier();
			break;
		case 'DELETESUPPLIER':
			delete_supplier();
			break;
		case 'GETALLOPERATORS':
			get_all_operators();
			break;
		case 'GETOPERATOR':
			select_operator();
			break;
		case 'UPDATEOPERATOR':
			update_operator();
			break;
		case 'ADDOPERATOR':
			create_operator();
			break;
		case 'DELETEOPERATOR':
			delete_operator();
			break;
		case 'GETALLDEPARTMENTS':
			get_all_departments();
			break;
		case 'GETDEPARTMENT':
			select_department();
			break;
		case 'UPDATEDEPARTMENT':
			update_department();
			break;
		case 'ADDDEPARTMENT':
			create_department();
			break;
		case 'DELETEDEPARTMENT':
			delete_department();
			break;
		case 'SEARCH':
			search();
			break;
		case 'TAKINGS':
			get_takings();
			break;
		case 'SAVETAKINGS':
			save_takings();
			break;
		case 'CLEARLABELS':
			clear_labels();
			break;
		case 'OPERATORLOGON':
			operator_login();
			break;
		case 'TOTALS':
			totals();
			break;
		case 'SENDMESSAGE':
			send_message();
			break;
		case 'GETMESSAGES':
			get_messages();
			break;
		case 'SAVELABELSTYLE':
			save_label();
			break;
		case 'GETLABELSTYLE':
			get_label_style();
			break;
		case 'GETLABELSTYLES':
			get_label_styles();
			break;
		case 'LOGIN':
			login();
			break;
		case 'LOGOUT':
			logout();
			break;
		case 'ISLOGGEDIN':
			is_logged_in();
			break;
		case 'GETPRODUCTLEVELS':
			get_product_levels();
			break;
		case 'GETPRODUCTSLEVELS':
			get_products_levels();
			break;
		case "CHANGEMAXSTOCKLEVEL":
			set_max_stock_level();
			break;
		case "CHANGECURRENTSTOCKLEVEL":
			set_current_stock_level();
			break;
		case "CREATEORDER":
			create_order();
			break;
		case "GETORDERS":
			get_orders();
			break;
		default:
			error_out('No such function');
	}
	function get_pdo_connection() {
		try {
			$config = parse_ini_file("opentill.ini");
			return new PDO('mysql:host=' . $config['database_host'] . ':' . $config['database_port'] . ';dbname=' . $config['database_name'], $config['database_username'], $config['database_password']);
		}
		catch(PDOException $e)
		{
			L($e->getMessage());
		}
	}
	function get_param($p, $d=false) {
		return (isset($_POST[$p]) ? $_POST[$p] : $d);
	}
	function error_out($m=null) {
		if ($m == null) {
			die (json_encode(array('success'=>false)));
		}
		die (json_encode(array('success'=>false, 'reason'=>$m)));
	}
	function success_out() {
		die (json_encode(array('success'=>true)));
	}
	function login() {
		$email = get_param('email', null);
		$password = get_param('password', null);
		if (($email == null) || ($password == null)) {
			error_out("missing parameters");
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT id, name FROM kvs_operators WHERE LCASE(email) = LCASE(?) AND passwordHash = ? LIMIT 1');
		$stmt->bindValue(1, $email);
		$stmt->bindValue(2, hash("sha512", $password));
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			L("User (" . $rs["id"] . ") " . $rs["name"] . " logged in successfully");
			$_SESSION["user"] = $rs["id"];
			success_out();
		}
		error_out("Email or password incorrect");
	}
	function logout() {
		unset($_SESSION["user"]);
		success_out();
	}
	function is_logged_in() {
		isset($_SESSION["user"]) ? success_out() : error_out();
	}
	function get_orders() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblorders.id, kvs_tblorders.supplier AS supplierId, kvs_tblsuppliers.name FROM kvs_tblorders LEFT JOIN kvs_tblsuppliers ON kvs_tblsuppliers.id = kvs_tblorders.supplier');
		$stmt->execute();
		$orders = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($orders, $rs);
		}
		die (json_encode(array("success"=>true, "products"=>$orders)));
	}
	function create_order() {
		$guid = GUID();
		$supplier = get_param('supplier', null);
		if ($supplier == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('INSERT INTO kvs_tblorders (id, supplier, created, updated) VALUES (?, ?, ?, ?)');
		if ($stmt->execute(array($guid, $supplier, time(), time()))) {
			die (json_encode(array("success"=>true)));
		}
		error_out();
	}
	function save_label() {
		$id = get_param('id', null);
		$name = get_param('name', null);
		$json = get_param('json', null);
		$newStyle = false;
		if ($id == null) {
			$newStyle = true;
			$id = GUID();
		}
		if (($json == null) || ($name == null)) {
			error_out('missing parameters');
		}
		$db = get_pdo_connection();
		$stmt = null;
		if ($newStyle) {
			$stmt = $db->prepare('INSERT kvs_labels (id, name, json, updated, created) VALUES (?, ?, ?, ?, ?)');
			if ($stmt->execute(array($id, $name, $json, time(), time()))) {
				die (json_encode(array("success"=>true, "guid"=>$id)));
			}
		}
		else {
			$stmt = $db->prepare('UPDATE kvs_labels SET name=?, json=?, updated=? WHERE id=?');
			$stmt->execute(array($name, $json, time(), $id));
			if ($stmt->rowCount() > 0) {
				die (json_encode(array("success"=>true)));
			}
		}
		error_out();
	}
	function get_label_style() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing parameters');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT json FROM kvs_labels WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			die (json_encode(array("success"=>true, "style"=>$rs["json"])));
		}
		error_out();
	}
	function decrement_product_level($product, $decrement_amount=1) {
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblproducts SET current_stock=current_stock-? WHERE id = ? AND current_stock > 0');
		$stmt->bindValue(1, $decrement_amount);
		$stmt->bindValue(2, $product);
		$stmt->execute();
	}
	function increment_product_level($product, $increment_amount=1) {
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblproducts SET current_stock=current_stock+? WHERE id = ?');
		$stmt->bindValue(1, $increment_amount);
		$stmt->bindValue(2, $product);
		$stmt->execute();
	}
	function set_max_stock_level() {
		$id = get_param('id', null);
		$amount = get_param('amount', null);
		if ($id == null || $amount == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblproducts SET max_stock=? WHERE id = ?');
		$stmt->bindValue(1, $amount);
		$stmt->bindValue(2, $id);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function set_current_stock_level() {
		$id = get_param('id', null);
		$amount = get_param('amount', null);
		if ($id == null || $amount == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblproducts SET current_stock=? WHERE id = ?');
		$stmt->bindValue(1, $amount);
		$stmt->bindValue(2, $id);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function get_products_levels() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT id, name, current_stock, max_stock FROM kvs_tblproducts WHERE current_stock < max_stock');
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$arr[$rs["id"]] = $rs;
		}
		die (json_encode(array("success"=>true, "products"=>$arr)));
	}
	function get_product_levels() {
		$id = get_param("id", null);
		if ($id == null) {
			error_out("missing fields");
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblproducts.id, kvs_tblproducts.name, kvs_inventory_levels.max_stock, kvs_inventory_levels.max_display, kvs_inventory_levels.lowest_reorder, kvs_inventory_levels.current_display, kvs_inventory_levels.current_stock FROM kvs_inventory_levels LEFT JOIN kvs_tblproducts ON kvs_tblproducts.id = kvs_inventory_levels.product WHERE kvs_tblproducts.id=? LIMIT 1');
		$stmt->bindValue(1, $id);
		$stmt->execute();
		$arr = array();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			die (json_encode(array("success"=>true, "product"=>$rs)));
		}
		error_out();
	}
	function get_label_styles() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT id, name FROM kvs_labels');
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$arr[$rs["id"]] = $rs["name"];
		}
		die (json_encode(array("success"=>true, "styles"=>$arr)));
	}
	function send_message() {
		$sender = get_param('from', null);
		$message = get_param('message', null);
		$recipient = get_param('to', null);
		if (($sender == null) || ($message == null)) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$guid = GUID();
		$stmt = $db->prepare('INSERT kvs_tblchat (id, sender, recipient, message, updated, created) VALUES (?, ?, ?, ?, ?, ?)');
		if ($stmt->execute(array($guid, $sender, $recipient, $message, time(), time()))) {
			die (json_encode(array("success"=>true, "guid"=>$guid)));
		}
		error_out();
	}
	function get_messages() {
		$operator = get_param('operator', null);
		$time = get_param('time', null);
		if ($operator == null || $time == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('(SELECT kvs_tblchat.id,  kvs_tblchat.sender AS "senderId", kvs_tblchat.recipient as "recipientId", a.name AS "senderName", IFNULL(b.name, "All") AS recipientName, kvs_tblchat.message, kvs_tblchat.created FROM kvs_tblchat LEFT JOIN kvs_operators a ON a.id = kvs_tblchat.sender LEFT JOIN kvs_operators b ON b.id = kvs_tblchat.recipient WHERE (kvs_tblchat.recipient = ? OR kvs_tblchat.sender = ? OR kvs_tblchat.recipient = "All") AND kvs_tblchat.created > ? ORDER BY kvs_tblchat.created DESC LIMIT 20) ORDER BY created ASC');
		$stmt->bindValue(1, $operator, PDO::PARAM_STR);
		$stmt->bindValue(2, $operator, PDO::PARAM_STR);
		$stmt->bindValue(3, $time, PDO::PARAM_INT);
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($arr, $rs);
		}
		die (json_encode(array("success"=>true, "messages"=>$arr)));
	}
	function insert_product(&$barcode, $name='Unknown Item') {
		$db = get_pdo_connection();
		$guid = GUID();
		$stmt = $db->prepare('INSERT IGNORE INTO kvs_tblproducts (id, barcode, name, created) VALUES (?, ?, ?, ?)');
		if ($stmt->execute(array($guid, $barcode, $name, time()))) {
			return $guid;
		}
	}
	function operator_login() {
		$code = get_param('code', null);
		if ($code == null) {
			error_out('missing fields');
		}
		L($code . 'attemping to login');
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_operators.id, kvs_operators.name FROM kvs_operators WHERE code = ? AND deleted = 0 LIMIT 1');
		$stmt->bindValue(1, $code, PDO::PARAM_STR);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			L("Operator " . $rs['name'] . '(' . $rs['id'] . ') logged in successfully');
			die (json_encode(array("success"=>true, "name"=>$rs["name"], "id"=>$rs["id"])));
		}
		error_out();
	}
	function get_item_from_UPC(&$barcode) {
		return false; //to speed up the till
		$key = '74694ba697ec53959d8abf15a026e04d';
		$json = json_decode(file_get_contents("http://api.upcdatabase.org/json/$key/$barcode"));
		if ($json->valid == "false") {
			return false; //Not found
		}
		if ($json->barcode !== $barcode) {
			return false; //Sometimes an incorrect product will be returned
		}
		if (strlen(trim($json->itemname)) <= 1) {
			insert_product($json->number, $json->description);
		}
		else {
			insert_product($json->number, $json->itemname);
		}
		return true;
	}
	function get_item_from_barcode(&$barcode) {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblproducts.* FROM kvs_tblproducts WHERE barcode = ? LIMIT 1');
		$stmt->bindValue(1, $barcode, PDO::PARAM_STR);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			return $rs;
		}
		return false;
	}
	function barcode() {
		$barcode = get_param('number', null);
		$units = get_param('units', null);
		if ($barcode == null) {
			error_out('no barcode');
		}
		if (!is_numeric($barcode)) {
			error_out('invalid barcode');
		}
		if ($rs = get_item_from_barcode($barcode)) {
			die (json_encode($rs));
		}
		if (get_item_from_UPC($barcode)) {
			die (json_encode(get_item_from_barcode($barcode)));
		}
		insert_product($barcode);
		$json = get_item_from_barcode($barcode);
		$json['isNew'] = true;
		die (json_encode($json));
	}
	
	function get_departments() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblcatagories.id, kvs_tblcatagories.name, kvs_tblcatagories.shorthand, kvs_tblcatagories.colour FROM kvs_tblcatagories WHERE deleted = 0 ORDER BY orderNum ASC');
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($arr, $rs);
		}
		die (json_encode($arr));
	}
	function GUID() {
		//http://php.net/manual/en/function.com-create-guid.php
		if (function_exists('com_create_guid') === true)
		    return trim(com_create_guid(), '{}');
		$data = openssl_random_pseudo_bytes(16);
		$data[6] = chr(ord($data[6]) & 0x0f | 0x40); // set version to 0100
		$data[8] = chr(ord($data[8]) & 0x3f | 0x80); // set bits 6-7 to 10
		return vsprintf('%s%s-%s-%s-%s-%s%s%s', str_split(bin2hex($data), 4));
	}
	function cancel_transaction() {
		$id = get_param('transaction_id', null);
		if (!transaction_exists($id)) {
			error_out('transaction does not exist');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('DELETE FROM kvs_transactions WHERE (id = ? AND ended = 0) LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute() ? success_out() : error_out();
	}
	function transaction_exists(&$id) {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT 1 FROM kvs_transactions WHERE (id = ? AND ended = 0) LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute();
		return ($stmt->rowCount() > 0);
	}
	function get_transaction_products() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblproducts.id, kvs_tblproducts.name,  kvs_transactiontoproducts.price, IFNULL(kvs_tblproducts.name, kvs_tblcatagories.name) as "name" FROM kvs_transactiontoproducts LEFT JOIN kvs_tblproducts ON kvs_tblproducts.id = kvs_transactiontoproducts.product_id LEFT JOIN kvs_tblcatagories ON kvs_tblcatagories.id = kvs_transactiontoproducts.department WHERE kvs_transactiontoproducts.transaction_id = ?');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($arr, $rs);
		}
		die(json_encode(array('success'=>true, 'products'=>$arr)));
	}
	function get_transactions() {
		$admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		$start = get_param("start", null);
		$end = get_param("end", null);
		if ($start == null || ($end == null)) {
			error_out("missing fields");
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_transactiontoproducts.transaction_id AS "id", ANY_VALUE(kvs_operators.name) AS cashier, COUNT(kvs_transactiontoproducts.transaction_id) AS "#Products", ANY_VALUE(kvs_transactions.card) AS "card", ANY_VALUE(kvs_transactions.ended) AS "ended", ANY_VALUE(kvs_transactions.cashback) AS "cashback", ANY_VALUE(kvs_transactions.money_given) AS "money_given", ANY_VALUE(kvs_transactions.payee) AS "payee", ANY_VALUE(kvs_transactions.type) AS "type", ANY_VALUE(kvs_transactions.total) AS "total" FROM kvs_transactions LEFT JOIN kvs_operators ON kvs_operators.id = kvs_transactions.cashier INNER JOIN kvs_transactiontoproducts ON kvs_transactiontoproducts.transaction_id = kvs_transactions.id WHERE (ANY_VALUE(kvs_transactions.ended) BETWEEN ? AND ?) AND ANY_VALUE(kvs_transactions.cashier) NOT IN (?) GROUP BY kvs_transactiontoproducts.transaction_id ORDER BY ANY_VALUE(kvs_transactions.ended)');
		$stmt->bindValue(1, $start, PDO::PARAM_INT);
		$stmt->bindValue(2, $end, PDO::PARAM_INT);
		$stmt->bindValue(3, $admin, PDO::PARAM_STR);
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($arr, $rs);
		}
		die (json_encode(array("success"=>true, "start"=>$start, "end"=>$end, "transactions"=>array_merge($arr, get_payouts()))));	
	}
	function get_payouts() {
		$admin = 'a10f653a-6c20-11e7-b34e-426562cc935f';
		$start = get_param('start', null);
		$end = get_param('end', null);
		if (($start == null) || ($end == null)) {
			error_out("missing fields");
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_transactions.id, kvs_operators.name AS cashier, kvs_transactions.card, kvs_transactions.ended, kvs_transactions.cashback, kvs_transactions.money_given, kvs_transactions.payee, kvs_transactions.type, kvs_transactions.total FROM kvs_transactions LEFT JOIN kvs_operators ON kvs_operators.id = kvs_transactions.cashier WHERE (kvs_transactions.started > ? AND kvs_transactions.ended < ?) AND kvs_transactions.cashier NOT IN (?) AND (kvs_transactions.ended > 0) AND type = ? ORDER BY kvs_transactions.ended');
		$stmt->bindValue(1, $start, PDO::PARAM_INT);
		$stmt->bindValue(2, $end, PDO::PARAM_INT);
		$stmt->bindValue(3, $admin, PDO::PARAM_STR);
		$stmt->bindValue(4, 'PAYOUT', PDO::PARAM_STR);
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($arr, $rs);
		}
		return $arr;
	}
	function start_transaction() {
		$operator = get_param('cashier_id', null);
		if ($operator == null)
			error_out('missing fields');
		$db = get_pdo_connection();
		$epoch = time();
		$guid = GUID();
		$stmt = $db->prepare('INSERT INTO kvs_transactions (id, started, cashier) VALUES (?, ?, ?)');
		if ($stmt->execute(array($guid, $epoch, $operator))) {
			L('Transaction (' . $guid . ') STARTED by operator (' . $operator . ')');
			die (json_encode(array('success'=>true, 'id'=>$guid, 'timeStart'=>$epoch)));
		}
		error_out();
	}
	function insert_transaction_product(&$db, $tranId, $pId, $price, $department) {
		$none_catagory = "5b82f89a-7b71-11e7-b34e-426562cc935f";
		$stmt = $db->prepare('INSERT INTO kvs_transactiontoproducts (id, transaction_id, product_id, price, department, created) VALUES (?, ?, ?, ?, ?, ?)');
		if ($stmt->execute(array(GUID(), $tranId, $pId, $price, $department==null ? $none_catagory : $department, time()))) {
			return true;
		}
		return false;
	}
	function write_transaction_file_to_database($id, $json=null, $type="REFUND") {
		if ($json==null) {
			error_out('Transaction file not formatted correctly');
		}
		$json = json_decode($json);
		$db = get_pdo_connection();
		$db->beginTransaction();
		try {
			foreach ($json->products as $product) {
				if ($type == "PURCHASE") {
					decrement_product_level($product->id, $product->quantity);
				}
				if ($type == "REFUND") {
					increment_product_level($product->id, $product->quantity);
				}	
				while ($product->quantity-- > 0) {
					if (!insert_transaction_product($db, $id, ($product->inDatabase ? $product->id : null), $product->price, $product->department)) {
						throw new Exception();
					}
				}
			}
			$db->commit();
		}
		catch (Exception $e) {
			$db->rollBack();
			return false;
		}
		return true;
	}
	function complete_transaction() {
		$id = get_param('id', null);
		$money_given = get_param('money_given', 0.00);
		$total = get_param('total', 0.00);
		$card = get_param('card_given', 0.00);
		$cashback = get_param('cashback', 0.00);
		$type = get_param('type', null);
		$payee = get_param('payee', null);
		$cashier_id = get_param('cashier', null);
		$json = get_param('json', null);
		if ($id == null) {
			error_out();
		}
		if (!transaction_exists($id)) {
			error_out('transaction does not exist');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_transactions SET ended = ?, total = ?, cashback=?, money_given = ?, card = ?, type=?, payee=? WHERE id=? AND cashier=?');
		if (write_transaction_file_to_database($id, $json)) {
			if ($stmt->execute(array(time(), $total, $cashback, $money_given, $card, $type, $payee, $id, $cashier_id))) {
				L("Transaction ($id) COMPLETED by operator ($cashier_id)");
				success_out();
			}
		}
		L("Failed to write transaction($id) to database, rolling back");
		error_out();
	}
	function update() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing id');
		}
		$name = get_param('name', null);
		$price = get_param('price', null);
		$department = get_param('department', null);
		$operator = get_param('cashier', null);
		if ($name==null || $price==null || $department==null || $operator==null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblproducts SET name = ?, price = ?, department=?, updated = ? WHERE id=?');
		if ($stmt->execute(array($name, $price, $department, time(), $id))) {
			L("Product ($id) UPDATED by operator ($operator)");
			success_out();
		}
		error_out();
	}
	function get_supplier_from_order($id) {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblorders.supplier FROM kvs_tblorders WHERE kvs_tblorders.id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			return $rs["supplier"];
		}
		return null;
	}
	function print_label() {
		$id = get_param('id', false);
		if (!$id) {
			error_out();
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblproducts SET labelprinted = ? WHERE id = ?');
		if ($stmt->execute(array(1, $id))) {
			success_out();
		}
		error_out();
	}
	function search() {
		$search = get_param('search', false);
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT * FROM kvs_tblproducts WHERE LOWER(name) LIKE LOWER(?) ORDER BY name LIMIT 20');
		$stmt->bindValue(1, "%$search%", PDO::PARAM_STR);
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$arr[] = $rs;
		}
		die (json_encode($arr));
	}
	function get_takings() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_takings.id, kvs_tbldepartments.name, kvs_takings.amount, DATE_FORMAT( FROM_UNIXTIME( updated ) , "%Y-%m-%e") AS "created" FROM kvs_takings RIGHT JOIN kvs_tbldepartments ON kvs_takings.department = kvs_tbldepartments.id');
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$created=$rs['created'];
			if (!isset($arr[$created])) {
				$arr[$created] = array();
			}
			$arr[$created][$rs['name']] = $rs["amount"];
		}
		die (json_encode($arr));
	}
	function save_takings() {
		$jsonString = get_param('json', null);
		$date = get_param('date', null);
		if ($date==null || $json_string==null) {
			error_out();
		}
		$json = json_decode($jsonString);
		$db = get_pdo_connection();
		$stmt = $db->prepare('INSERT INTO kvs_tbltakings (id, department, amount, created) VALUES (?,?,?,?)');
		$success = true;
		foreach ($json as $key=>$value) {
			$success ? $stmt->execute(array(GUID(), $key, $value, $date)) : false;
		}
		die (json_encode(array("success"=>$success)));
	}
	function clear_labels() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblproducts SET labelprinted=0 WHERE labelprinted=1');
		if ($stmt->execute())
			success_out();
		error_out();
	}
	function refunds() {
		$admin = 'a10f653a-6c20-11e7-b34e-426562cc935f';
		$start = get_param('start', null);
		$end = get_param('end', null);
		if ($start == null || $end == null) {
			error_out("missing fields");
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_transactiontoproducts.department, SUM(kvs_transactiontoproducts.price) AS "amount" FROM kvs_transactiontoproducts LEFT JOIN kvs_transactions ON kvs_transactions.id = kvs_transactiontoproducts.transaction_id WHERE (kvs_transactions.started > ? AND kvs_transactions.ended < ?) AND kvs_transactions.cashier NOT IN (?) AND kvs_transactions.type in (?) AND (kvs_transactions.ended > 0) GROUP BY kvs_transactiontoproducts.department');
		$stmt->bindValue(1, $start, PDO::PARAM_INT);
		$stmt->bindValue(2, $end, PDO::PARAM_INT);
		$stmt->bindValue(3, $admin, PDO::PARAM_STR);
		$stmt->bindValue(4, 'REFUND', PDO::PARAM_STR);
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$arr[$rs['department']] = floatval($rs['amount']);
		}
		return $arr;
	}
	function totals() {
		$none_catagory = "5b82f89a-7b71-11e7-b34e-426562cc935f";
		$admin = 'a10f653a-6c20-11e7-b34e-426562cc935f';
		$start = get_param('start', null);
		$end = get_param('end', null);
		$type = get_param('type', null);
		if ($start == null || $end == null || $type == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$db->query('SET SQL_BIG_SELECTS=1');
		$stmt = $db->prepare('SELECT DATE(FROM_UNIXTIME(kvs_transactions.ended)) AS "date", kvs_transactiontoproducts.department, SUM(kvs_transactiontoproducts.price) AS "amount" FROM kvs_transactiontoproducts LEFT JOIN kvs_transactions ON kvs_transactiontoproducts.transaction_id = kvs_transactions.id WHERE (kvs_transactions.started > ? AND kvs_transactions.ended < ?) AND kvs_transactions.cashier NOT IN (?) AND kvs_transactions.type in (?) AND (kvs_transactions.ended > 0) GROUP BY kvs_transactiontoproducts.department, DATE(FROM_UNIXTIME(kvs_transactions.ended)) ORDER BY DATE(FROM_UNIXTIME(kvs_transactions.ended)) DESC');
		$stmt->bindValue(1, $start,PDO::PARAM_INT);
		$stmt->bindValue(2, $end,PDO::PARAM_INT);
		$stmt->bindValue(3, $admin,PDO::PARAM_STR);
		$stmt->bindValue(4, $type,PDO::PARAM_STR);
		if (!$stmt->execute()) {
			error_out($stmt->errorInfo());
		}
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			if ($type == 'PAYOUT') {
				$arr['payout'] = floatval($rs['amount']);
				break;
			}
			if (!isset($arr[$rs['date']])) {
				$arr[$rs['date']] = array();
			} 
			$arr[$rs['date']][$rs['department']] = floatval($rs['amount']);
		}
		if ($type == 'PAYOUT') {
			die (json_encode(array('success'=>true, 'start'=>$start, 'end'=>$end, 'payouts'=>$arr)));
		}
		if ($type == 'REFUND') {
			die (json_encode(array('success'=>true, 'start'=>$start, 'end'=>$end, 'payouts'=>$arr)));
		}
		/*$refunds = refunds();
		foreach ($arr as $date) {
			foreach ($arr as $department=>$amount) {
				$arr[$date][$department] = ($amount - (isset($refunds[$department]) ? $refunds[$department] : 0.00));
			}
		}*/
		die (json_encode(array('success'=>true, 'start'=>$start, 'end'=>$end, 'totals'=>$arr)));
	}
	function get_all_suppliers() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblsuppliers.* FROM kvs_tblsuppliers WHERE kvs_tblsuppliers.deleted = 0 ORDER BY kvs_tblsuppliers.name');
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$arr[$rs["id"]] = $rs;
		}
		die (json_encode(array("success"=>true, "suppliers"=>$arr)));
	}
	function select_supplier() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing id');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT * FROM kvs_tblsuppliers WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			die (json_encode(array("success"=>true, "supplier"=>$rs)));
		}
		error_out();
	}
	function create_supplier() {
		$name = get_param('name', null);
		$telephone = get_param('telephone', null);
		$email = get_param('email', null);
		$website = get_param('website', null);
		$comments = get_param('comments', null);
		if ($name == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$guid = GUID();
		$stmt = $db->prepare('INSERT INTO kvs_tblsuppliers (id, name, telephone, email, website, comments, created, updated) VALUES (?,?,?,?,?,?,?,?)');
		$stmt->bindValue(1, $guid, PDO::PARAM_STR);
		$stmt->bindValue(2, $name, PDO::PARAM_STR);
		$stmt->bindValue(3, $telephone, PDO::PARAM_STR);
		$stmt->bindValue(4, $email, PDO::PARAM_STR);
		$stmt->bindValue(5, $website, PDO::PARAM_STR);
		$stmt->bindValue(6, $comments, PDO::PARAM_STR);
		$stmt->bindValue(7, time(), PDO::PARAM_INT);
		$stmt->bindValue(8, time(), PDO::PARAM_INT);
		if ($stmt->execute()) {
			die (json_encode(array("success"=>true, "id"=>$guid)));
		}
		error_out();
	}
	function update_supplier() {
		$id = get_param('id', null);
		$name = get_param('name', null);
		$telephone = get_param('telephone', null);
		$email = get_param('email', null);
		$website = get_param('website', null);
		$comments = get_param('comments', null);
		if (($id == null) || ($name == null)) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblsuppliers SET name = ?, telephone = ?, email = ?, website = ?, comments = ?, updated = ? WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $name, PDO::PARAM_STR);
		$stmt->bindValue(2, $telephone, PDO::PARAM_STR);
		$stmt->bindValue(3, $email, PDO::PARAM_STR);
		$stmt->bindValue(4, $website, PDO::PARAM_STR);
		$stmt->bindValue(5, $comments, PDO::PARAM_STR);
		$stmt->bindValue(6, time(), PDO::PARAM_INT);
		$stmt->bindValue(7, $id, PDO::PARAM_STR);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function delete_supplier() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblsuppliers SET deleted = 1 WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function get_all_operators() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_operators.* FROM kvs_operators WHERE kvs_operators.deleted = 0 ORDER BY kvs_operators.name');
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$arr[] = $rs;
		}
		die (json_encode(array("success"=>true, "operators"=>$arr)));
	}
	function select_operator() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing id');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT * FROM kvs_operators WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			die (json_encode(array("success"=>true, "operator"=>$rs)));
		}
		error_out();
	}
	function create_operator() {
		$name = get_param('name', null);
		$telephone = get_param('telephone', null);
		$email = get_param('email', null);
		$website = get_param('website', null);
		$comments = get_param('comments', null);
		if ($name == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$guid = GUID();
		$code = explode("-", GUID())[0]; //generates the first part of a UUID for a barcode key
		$stmt = $db->prepare('INSERT INTO kvs_operators (id, code, name, telephone, email, created, updated) VALUES (?,?,?,?,?,?,?)');
		$stmt->bindValue(1, $guid, PDO::PARAM_STR);
		$stmt->bindValue(2, $code, PDO::PARAM_STR);
		$stmt->bindValue(3, $name, PDO::PARAM_STR);
		$stmt->bindValue(4, $telephone, PDO::PARAM_STR);
		$stmt->bindValue(5, $email, PDO::PARAM_STR);
		$stmt->bindValue(6, time(), PDO::PARAM_INT);
		$stmt->bindValue(7, time(), PDO::PARAM_INT);
		if ($stmt->execute()) {
			die (json_encode(array("success"=>true, "id"=>$guid)));
		}
		error_out();
	}
	function update_operator() {
		$id = get_param('id', null);
		$name = get_param('name', null);
		$password = get_param('password', null);
		$telephone = get_param('telephone', null);
		$email = get_param('email', null);
		$website = get_param('website', null);
		$comments = get_param('comments', null);
		if (($id == null) || ($name == null) || ($password == null)) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_operators SET name = ?, passwordHash = ?, telephone = ?, email = ?, updated = ? WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $name, PDO::PARAM_STR);
		$stmt->bindValue(2, hash("sha512", $password), PDO::PARAM_STR);
		$stmt->bindValue(3, $telephone, PDO::PARAM_STR);
		$stmt->bindValue(4, $email, PDO::PARAM_STR);
		$stmt->bindValue(5, time(), PDO::PARAM_INT);
		$stmt->bindValue(6, $id, PDO::PARAM_STR);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function delete_operator() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_operators SET deleted = 1 WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function get_all_departments() {
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblcatagories.* FROM kvs_tblcatagories WHERE kvs_tblcatagories.deleted = 0 ORDER BY kvs_tblcatagories.name');
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			$arr[] = $rs;
		}
		die (json_encode(array("success"=>true, "departments"=>$arr)));
	}
	function select_department() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing id');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT * FROM kvs_tblcatagories WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			die (json_encode(array("success"=>true, "department"=>$rs)));
		}
		error_out();
	}
	function create_department() {
		$name = get_param('name', null);
		$colour = get_param('colour', null);
		$shorthand = get_param('shorthand', null);
		$comments = get_param('comments', null);
		if ($name == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$guid = GUID();
		$stmt = $db->prepare('INSERT INTO kvs_tblcatagories (id, name, shorthand, colour, comments, created, updated) VALUES (?,?,?,?,?,?,?)');
		$stmt->bindValue(1, $guid, PDO::PARAM_STR);
		$stmt->bindValue(2, $name, PDO::PARAM_STR);
		$stmt->bindValue(3, $shorthand, PDO::PARAM_STR);
		$stmt->bindValue(4, $colour, PDO::PARAM_STR);
		$stmt->bindValue(5, $comments, PDO::PARAM_STR);
		$stmt->bindValue(6, time(), PDO::PARAM_INT);
		$stmt->bindValue(7, time(), PDO::PARAM_INT);
		if ($stmt->execute()) {
			die (json_encode(array("success"=>true, "id"=>$guid)));
		}
		error_out();
	}
	function update_department() {
		$id = get_param('id', null);
		$name = get_param('name', null);
		$colour = get_param('colour', null);
		$shorthand = get_param('shorthand', null);
		$comments = get_param('comments', null);
		if (($id == null) || ($name == null)) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblcatagories SET name = ?, shorthand = ?, colour = ?, comments = ?, updated = ? WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $name, PDO::PARAM_STR);
		$stmt->bindValue(2, $shorthand, PDO::PARAM_STR);
		$stmt->bindValue(3, $colour, PDO::PARAM_STR);
		$stmt->bindValue(4, $comments, PDO::PARAM_STR);
		$stmt->bindValue(5, time(), PDO::PARAM_INT);
		$stmt->bindValue(6, $id, PDO::PARAM_STR);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function delete_department() {
		$id = get_param('id', null);
		if ($id == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('UPDATE kvs_tblcatagories SET deleted = 1 WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		if ($stmt->execute()) {
			success_out();
		}
		error_out();
	}
	function get_product() {
		$id = get_param("id", null);
		if ($id == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT kvs_tblproducts.* FROM kvs_tblproducts WHERE id = ? LIMIT 1');
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			die (json_encode(array("success"=>true, "product"=>$rs)));
		}
		error_out();
	}
	function get_product_transactions() {
		$id = get_param("id", null);
		$start = get_param("start", null);
		$end = get_param("end", null);
		if ($id == null) {
			error_out('missing fields');
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare("SELECT * FROM `kvs_transactiontoproducts` WHERE product_id = ? AND created BETWEEN ? AND ?");
		$stmt->bindValue(1, $id, PDO::PARAM_STR);
		$stmt->bindValue(2, $start, PDO::PARAM_INT);
		$stmt->bindValue(3, $end, PDO::PARAM_INT);
		if (!$stmt->execute()) {
			error_out();
		}
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($arr, $rs);
		}
		die (json_encode(array("success"=>true, "sales"=>$arr)));
	}	
	function L($m) {
		$myfile = fopen('../log/' . date('Y-m-d', time()) . '.log', 'a');
		fwrite($myfile, "\n". date('Y-m-d h:i:s a', time()) . ': '. $m);
		fclose($myfile);
	}
?>
