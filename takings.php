<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript" src="https://www.goldstandardresearch.co.uk/thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="https://www.goldstandardresearch.co.uk/thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="https://www.goldstandardresearch.co.uk/thirdParty/accounting/js/accounting.min.js"></script>
	<!--bootstrap-->
	<!--<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/js/bootstrap.min.js"></script>-->
<!--Datatables-->					
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs-3.3.7/jszip-3.1.3/pdfmake-0.1.27/dt-1.10.15/b-1.3.1/b-html5-1.3.1/b-print-1.3.1/fc-3.2.2/r-2.1.1/datatables.min.css"/>
	<script type="text/javascript" src="https://cdn.datatables.net/v/bs-3.3.7/jszip-3.1.3/pdfmake-0.1.27/dt-1.10.15/b-1.3.1/b-html5-1.3.1/b-print-1.3.1/fc-3.2.2/r-2.1.1/datatables.min.js"></script>
	<style>
		.dataTables_filter {
			float:right;
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
		<section class="row text-center">
			<section class="col-md-2">
				<div class="panel panel-default">
				  <div class="panel-body">
					<h6>Todays Payouts</h6>
					<h4 id="payouts">0.00</h4>
				  </div>
				</div>
			</section>
			<section class="col-md-2">
				<div class="panel panel-default">
				  <div class="panel-body">
					<h6>Todays Refunds</h6>
					<h4 id="refunds">0.00</h4>
				  </div>
				</div>
			</section>
			<section class="col-md-2">
				<div class="panel panel-default">
				  <div class="panel-body">
					<h6>Todays Card Given</h6>
					<h4 id="card-payments">0.00</h4>
				  </div>
				</div>
			</section>
			<section class="col-md-2">
				<div class="panel panel-default">
				  <div class="panel-body">
					<h6>Todays Takings</h6>
					<h4 id="takings">0.00</h4>
				  </div>
				</div>
			</section>
			<section class="col-md-2">
				<div class="panel panel-default">
				  <div class="panel-body">
					<h6>Todays Cashback</h6>
					<h4 id="cashback">0.00</h4>
				  </div>
				</div>
			</section>
			<section class="col-md-2">
				<div class="panel panel-default">
				  <div class="panel-body">
					<h6>Todays Cash ID</h6>
					<h4 id="cash-id">0.00</h4>
				  </div>
				</div>
			</section>
		</section>
	</section>
</body>
</html>