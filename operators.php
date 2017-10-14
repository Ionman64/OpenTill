<!DOCTYPE html>
<html>
	<head>
		<title>KVS-Operators</title>
		<link rel="stylesheet" href="css/labels.css"/>
		<link REL="SHORTCUT ICON" href="img/site_icon.png"/>
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
		<!--accountingJS-->
		<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
		<!--jscrollpane-->
		<script type="text/javascript" src="thirdParty/jscrollpane/js/jquery.jscrollpane.min.js"></script>
		<link rel="stylesheet" href="thirdParty/jscrollpane/css/jquery.jscrollpane.css"/>
		<!--Datatables-->					
		<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs-3.3.7/jszip-3.1.3/pdfmake-0.1.27/dt-1.10.15/b-1.3.1/b-html5-1.3.1/b-print-1.3.1/fc-3.2.2/r-2.1.1/datatables.min.css"/>
		<script type="text/javascript" src="https://cdn.datatables.net/v/bs-3.3.7/jszip-3.1.3/pdfmake-0.1.27/dt-1.10.15/b-1.3.1/b-html5-1.3.1/b-print-1.3.1/fc-3.2.2/r-2.1.1/datatables.min.js"></script>
		<style>
			tbody tr:hover {
				cursor:pointer;
				background:#bbb;
			}
		</style>
		<script type="text/javascript" src="js/operators.js"></script>
	</head>
	<body>	
		<section id="navigation" class="navbar navbar-inverse navbar-fixed-top navigation no-print">
			<ul>
				<li style="text-align:right"><a href="index.php" class="btn btn-default btn-lg">Back to till</a></li>
				<li class="pull-right"><button id="add-operator" class="btn btn-default btn-lg">Add Operator</button></li>
			</ul>
		</section>
		<section class="container-fluid" style="padding-top:80px;">
			<section class="row">
				<section class="col-md-12" id="viewport">
					
				</section>
			</section>
		</section>
		<?php require("modals/operatorInfo.php") ?>
	</body>
</html>

