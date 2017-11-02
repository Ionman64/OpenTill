<?php
	session_start();
	if (!isset($_SESSION['user'])) {
		header('Location: login.php');
	}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<!--accountingJS-->
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--ChartJS-->
	<script type="text/javascript" src="thirdParty/chartJS/js/chart.bundle.js"></script>
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
	<!--bootstrap-->
	<!--<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/js/bootstrap.min.js"></script>-->
	<!--MUICSS-->
	<link href="thirdParty/mui-0.9.27/css/mui.min.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="thirdParty/mui-0.9.27/js/mui.min.js"></script>
	<!--Datatables-->					
	<link rel="stylesheet" type="text/css" href="thirdParty/DataTables/custom-build/custom.css"/>
	<script type="text/javascript" src="thirdParty/DataTables/custom-build/custom.js"></script>
	<script type="text/javascript" src="js/takings.js"></script>
	<script type="text/javascript" src="js/transactions.js"></script>
	<script type="text/javascript" src="js/operators.js"></script>
	<script type="text/javascript" src="js/suppliers.js"></script>
	<script type="text/javascript" src="js/inventory.js"></script>
	<script type="text/javascript" src="js/departments.js"></script>
	
	<script type="text/javascript" src="js/dashboard.js"></script>
	<link href="css/dashboard.css" rel="stylesheet" type="text/css"/>
	<title>KVS-Dashboard</title>
	<!--<script type="text/javascript" src="js/transactions.js"></script>-->
</head>
<body>
	<section class="container-fluid">
		<section class="row">
		<section class="col-md-12 no-padding">
			<section class="custom-navbar">
				<section class="container-fluid">
					<section class="row">
						<section class="col-md-12" style="padding-top:5px;">
							<section class="user pull-left">
								<div class="mui-dropdown">
									<img class="user-img" src="http://placehold.it/45x45" data-mui-toggle="dropdown"/>
									<ul class="mui-dropdown__menu" style="left:-30px;">
										<li><a href="#" id="logout">Logout</a></li>
									</ul>
								</div>
								<p class="user-text">Example User</p>
							</section>
							<ul class="mui-tabs__bar pull-right">
							  <li class="mui--is-active"><a data-mui-toggle="tab" data-mui-controls="pane-primary-home">Home</a></li>
							  <li><a data-mui-toggle="tab" data-mui-controls="pane-primary-overview">Overview</a></li>
							  <li><a data-mui-toggle="tab" data-mui-controls="pane-primary-takings">Takings</a></li>
							  <li><a data-mui-toggle="tab" data-mui-controls="pane-primary-transactions">Transactions</a></li>
							  <li><a data-mui-toggle="tab" data-mui-controls="pane-primary-operators">Operators</a></li>
							  <li><a data-mui-toggle="tab" data-mui-controls="pane-primary-inventory">Inventory</a></li>
							  <li><a data-mui-toggle="tab" data-mui-controls="pane-primary-suppliers">Suppliers</a></li>
							  <li><a data-mui-toggle="tab" data-mui-controls="pane-primary-departments">Departments</a></li>
							</ul>
						</section>
						<section class="col-md-2 col-md-offset-10">
							<button class="btn btn-default btn-lg pull-right hidden" id="logout" style="margin:10px">Logout</button>
						</section>
					</section>
				</section>
			</section>
			<div class="mui-tabs__pane mui--is-active" id="pane-primary-home">
				<?php require("views/home.php"); ?>
			</div>
			<div class="mui-tabs__pane" id="pane-primary-overview">
				<?php require("views/overview.php"); ?>
			</div>
			<div class="mui-tabs__pane" id="pane-primary-takings">
				<?php require("views/takings.php"); ?>
			</div>
			<div class="mui-tabs__pane" id="pane-primary-transactions">
				<?php require("views/transactions.php"); ?>
			</div>
			<div class="mui-tabs__pane" id="pane-primary-operators">
				<?php require("views/operators.php"); ?>
			</div>
			<div class="mui-tabs__pane" id="pane-primary-suppliers">
				<?php require("views/suppliers.php"); ?>
			</div>
			<div class="mui-tabs__pane" id="pane-primary-inventory">
				<?php require("views/inventory.php"); ?>
			</div>
			<div class="mui-tabs__pane" id="pane-primary-departments">
				<?php require("views/departments.php"); ?>
			</div>
		</section>
        </section>
    </section>
	<?php require_once("modals/transactionProducts.php"); ?>
	<?php require_once("modals/productModal.php"); ?>
</body>
</html>
