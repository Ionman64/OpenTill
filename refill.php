<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="img/site/icon.ico">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self' http://localhost:8888/drawer http://127.0.0.1:8888/drawer http://localhost:8888/receipt http://127.0.0.1:8888/receipt; style-src 'self' 'unsafe-inline'"/>
	<meta name="robots" content="noindex, nofollow"/>
	<meta name="viewport" content="width=device-width, user-scalable=no">
	<!--jQuery-->
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<!--bootstrap-->
	<link rel="stylesheet" href="thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="thirdParty/bootstrap/js/bootstrap.min.js"></script>
	<!--Bootbox-->
	<script src="thirdParty/bootbox/js/bootbox.min.js"></script>
	<!--moment-->
	<script src="thirdParty/moment/js/moment-with-locales.min.js"></script>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<!--touchspin-->
	<script src="thirdParty/touchspin/js/jquery.bootstrap-touchspin.min.js"></script>
	<link rel="stylesheet" href="thirdParty/touchspin/css/jquery.bootstrap-touchspin.min.css" />
<!--Datatables-->					
	<link rel="stylesheet" type="text/css" href="thirdParty/DataTables/custom-build/custom.css"/>
	<script type="text/javascript" src="thirdParty/DataTables/custom-build/custom.js"></script>
	<style>
		.dataTables_filter {
			float:right;
		}
		tbody tr:hover {
			cursor:pointer;
			background:#bbb;
		}
		.panel h2 {
			margin-top:0;
		}
		.sub-text {
			font-size:12px;
			color:#444;
			font-style:italic;
		}
		.product {
			margin-top:2px;
			background:#eee;
		}
	</style>
	<title>KVS-Refill</title>
	<script type="text/javascript" src="js/order.js"></script>
</head>
<body>
	<section class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid">
			<section class="row">
				<section class="col-md-2">
					<a href="index.php"><button class="navbar-btn btn btn-lg btn-default">Back to till</button></a>
				</section>
			</section>
		</div>
	</section>
	<section class="container-fluid" style="margin-top:65px;">
		<section class="row" style="border-bottom:2px solid #000">
			<section class="col-md-1">
				
			</section>
			<section class="col-md-5">
				<h4>Name</h4>
			</section>
			<section class="col-md-1">
				<h4>Max Stock<br><span class="sub-text">(Including Display)</span></h4>
			</section>
			<section class="col-md-1">
				<h4>In Stock<br><span class="sub-text">(Including Display)</span></h4>
			</section>
			<section class="col-md-1">
				
			</section>
			<section class="col-md-1">
				<h4>Max Display</h4>
			</section>
			<section class="col-md-1">
				<h4>On Display</h4>
			</section>
			<section class="col-md-1">
				
			</section>
		</section>
		<section class="row">
			<section class="col-md-12">
				<section id="table">
		
				</section>
			</section>
		</section>
	</section>
	<?php require_once("modals/takings.php"); ?>
</body>
</html>
