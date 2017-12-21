<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
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
	</style>
	<title>KVS-Takings</title>
	<script type="text/javascript" src="js/takings.js"></script>
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
		<section class="row">
			<section class="col-md-12">
				<section id="viewport">
		
				</section>
			</section>
		</section>
	</section>
	<?php require_once("modals/takings.php"); ?>
</body>
</html>
