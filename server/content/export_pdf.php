<?php
	function GUID() {
		return sprintf('%04X%04X-%04X-%04X-%04X-%04X%04X%04X', mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(16384, 20479), mt_rand(32768, 49151), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535));
	}
	$html = isset($_POST["html"]) ? $_POST["html"] : die (json_encode(array("success"=>false)));
	require 'thirdParty/mpdf70/vendor/mpdf/mpdf/mpdf.php';
	$mpdf = new Mpdf('utf-8', 'A4');
	$mpdf->setTitle("labels.pdf");
	//$mpdf->SetDisplayMode('fullpage');
	$mpdf->WriteHTML(file_get_contents('css/labels.css'),1);
	$mpdf->WriteHTML(file_get_contents('css/paper.css'),1);
	$mpdf->WriteHTML($html);
	$guid = GUID();
	$mpdf->Output("pdf/$guid.pdf");
	die (json_encode(array("success"=>true, "guid"=>$guid)));
?>
