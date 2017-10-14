<?php
	function out($m) {
		echo ("$m\n");
	}
	function truncate($string, $length, $dots = "...") {
		return (strlen($string) > $length) ? substr($string, 0, $length - strlen($dots)) . $dots : $string;
	}
	function get_param($p, $d=false) {
		return (isset($_POST[$p]) ? $_POST[$p] : $d);
	}
	function get_pdo_connection() {
		try {
			return new PDO('mysql:host=goldstandardresearch.co.uk.mysql;dbname=goldstandardresearch_co_uk_kvs', 'goldstandardresearch_co_uk_kvs', '&Psr&aWg7k');
		}
		catch(PDOException $e)
		{
			echo $e->getMessage(); //Will pump out the PDO exception error
		}
	}
	$db = get_pdo_connection();
	$jsonString = get_param("json", null);
	$json = null;
	try {
		if ($jsonString !== null) {
			$json = json_decode($jsonString);
		}
	}
	catch (Exception $ex) {
		die (json_encode(array("success"=>false)));
	}
	$sql;
	$numOfDepartments = count($json->departments);
	if ($numOfDepartments == 0) {
		$sql = ('SELECT * FROM kvs_tblproducts WHERE labelprinted = 1 ORDER BY name');
	}
	else {
		$sql = ('SELECT * FROM kvs_tblproducts WHERE labelprinted = 1 AND department IN (%^%) ORDER BY name');
	}
	
	if ($numOfDepartments > 0) {
		$prefix = ",";
		$query_string = "";
		for ($i=0;$i<$numOfDepartments;$i++) {
			$query_string = $query_string . "?" . $prefix;
		}
		$query_string = trim($query_string, $prefix);
		$sql = str_replace("%^%", $query_string, $sql);
	}
	$stmt = $db->prepare($sql);
	for ($i=0;$i<$numOfDepartments;$i++) {
		$stmt->bindValue($i+1, $json->departments[$i]);
	}
	$stmt->execute();
	$arr = array();
	while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
		array_push($arr, $rs);
	}
	die (json_encode($arr));
	if ($newPage) {
		out ('<section class="sheet padding-5mm">');
		out	('<article>');
		out ('<section class="center-output">');
		$newPage = false;
	}
	out ("<section class='product-label'>");
	out ("<label class='product-name'>" . truncate($rs['name'], 25) . "</label>");
	out ("<label class='product-price'>£" . number_format($rs['price'],2) . "</label>");
	out ("</section>");
	if ((++$i % 14 == 0) || ($i === $numOfResults)) {
		$endPage = true;
	}
	if ($endPage) {
		out("</section>");
		out("</article>");
		out("</section>");
		$endPage = false;
		$newPage = true;
	}
	if ($numOfResults == 0) {
		out ('<section class="sheet padding-10mm">');
		out	('<article>');
		out ("<section class='panel panel-info'>No Products to Print</section>");	
		out("</article>");
		out("</section>");
	}
?>
