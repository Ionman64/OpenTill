<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--bootstrap-->
	<!--<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/js/bootstrap.min.js"></script>-->
<!--Datatables-->					
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs-3.3.7/jszip-3.1.3/pdfmake-0.1.27/dt-1.10.15/b-1.3.1/b-html5-1.3.1/b-print-1.3.1/fc-3.2.2/r-2.1.1/datatables.min.css"/>
	<script type="text/javascript" src="https://cdn.datatables.net/v/bs-3.3.7/jszip-3.1.3/pdfmake-0.1.27/dt-1.10.15/b-1.3.1/b-html5-1.3.1/b-print-1.3.1/fc-3.2.2/r-2.1.1/datatables.min.js"></script>
	<style>
		body {
			overflow:none;
		}
		.dataTables_filter {
			float:right;
		}
		#viewport {
			padding:5px;
			margin-top:5px;
		}
		.sidebar {
			background: #aaa none repeat scroll 0 0;
			height:800px;
		}
		tbody tr:hover {
			cursor:pointer;
			background:#bbb;
		}
		.payin {
			background:#66ff66;
		}
		.payout {
			background:#ff6666;
		}
	</style>
	<title>KVS-Dashboard</title>
	<script type="text/javascript" src="js/transactions.js"></script>
</head>
<body>
	<section class="container-fluid" style="padding-top:60px;">
		<section class="row hidden" style="padding-top:5px;">
			<section class="col-md-3">
				<label>Date Start</label><br>
				<button class="btn btn-default btn-lg" id="dateFrom"></button>
				<button class="btn btn-default btn-lg" id="timeFrom"></button>
			</section>
			<section class="col-md-3">
				<label>Date Finish</label><br>
				<button class="btn btn-default btn-lg" id="dateTo"></button>
				<button class="btn btn-default btn-lg" id="timeTo"></button>
			</section>
		</section>
		<section class="row">
			<section class="col-md-2 hidden">
				<section class="sidebar">
					<ul>
						<li>Transactions</li>
					</ul>
				</section>
			</section>
			<section class="col-sm-12 col-md-10 col-md-offset-2">
				<section id="viewport" class="panel panel-default">
				
				</section>
			</section>
		</section>
	</section>
	<section class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid">
			<section class="row">
				<section class="col-md-2">
					<a href="index.php"><button class="navbar-btn btn btn-lg btn-default">Back to till</button></a>
				</section>
			</section>
		</div>
	</section>
	<?php require_once("modals/transactionProducts.php"); ?>
</body>
</html>
