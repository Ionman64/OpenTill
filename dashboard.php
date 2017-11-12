<?php
	session_start();
	if (!isset($_SESSION['user'])) {
		header('Location: login.php');
	}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8"/>
	<link rel="shortcut icon" href="img/site/icon.ico">
	<meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta name="viewport" content="width=device-width, initial-scale=1"/>
	<meta name="robots" content="noindex, nofollow"/>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<!--accountingJS-->
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--ChartJS-->
	<script type="text/javascript" src="thirdParty/chartJS/js/chart.bundle.js"></script>
	<!--bootstrap-->
	<link rel="stylesheet" href="thirdParty/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" href="thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="thirdParty/bootstrap/js/bootstrap.min.js"></script>
	<!--custom-->
	<script type="text/javascript" src="js/takings.js"></script>
	<script type="text/javascript" src="js/transactions.js"></script>
	<script type="text/javascript" src="js/operators.js"></script>
	<script type="text/javascript" src="js/suppliers.js"></script>
	<script type="text/javascript" src="js/inventory.js"></script>
	<script type="text/javascript" src="js/departments.js"></script>
	<script type="text/javascript" src="js/dashboard.js"></script>
	<link href="css/dashboard.css" rel="stylesheet" type="text/css"/>
	<title>KVS-Dashboard</title>
</head>
<body class="no-select">
	<section class="container-fluid">
		<section class="menu overlay overlay-ontop hidden" id="menu">
			<section class="menu-buttons">
				<section class="container-fluid">
				<section class="row">
					<section class="menu-button custom-btn-black text-center" data-page="pane-primary-takings">
						<i class="fa fa-bar-chart"></i>
						<section class="footer">
							<span>Takings</span>
						</section>
					</section>
					<section class="menu-button custom-btn-orange text-center" data-page="pane-primary-transactions">
						<i class="fa fa-tasks"></i>
						<section class="footer">
							<span>Transactions</span>
						</section>
					</section>
					<section class="menu-button custom-btn-purple text-center" data-page="pane-primary-inventory">
						<i class="fa fa-cubes"></i>
						<section class="footer">
							<span>Inventory</span>
						</section>
					</section>
					<section class="menu-button custom-btn-chocolate text-center" data-page="pane-primary-departments">
						<i class="fa fa-table"></i>
						<section class="footer">
							<span>Departments</span>
						</section>
					</section>
					<section class="menu-button custom-btn-violet text-center">
						<i class="fa fa-tags"></i>
						<section class="footer">
							<span>Labels</span>
						</section>
					</section>
					<br class="col-xs-hidden col-sm-hidden"/>
					<section class="menu-button btn-warning text-center" data-page="pane-primary-operators">
						<i class="fa fa-user"></i>
						<section class="footer">
							<span>Operators</span>
						</section>
					</section>
					<section class="menu-button btn-danger text-center" data-page="pane-primary-suppliers">
						<i class="fa fa-truck"></i>
						<section class="footer">
							<span>Suppliers</span>
						</section>
					</section>
					<section class="menu-button btn-success text-center" id="">
						<i class="fa fa-comments"></i>
						<section class="footer">
							<span>Chat</span>
						</section>
					</section>
					<section class="menu-button custom-btn-graphite text-center" id="logout">
						<i class="fa fa-sign-out"></i>
						<section class="footer">
							<span>Sign Out</span>
						</section>
					</section>
					<section class="menu-button btn-danger text-center" id="cancelMenu">
						<i class="fa fa-times"></i>
						<section class="footer">
							<span>Close Menu</span>
						</section>
					</section>
				</section>
			</section>
	</section>
	</section>
		<section class="row">
		<section class="col-md-12 no-padding">
			<section class="custom-navbar">
				<section class="container-fluid">
					<section class="row">
						<section class="col-lg-10 col-md-10 col-sm-10 col-xs-10" style="padding-top:5px;">
							<p id="menu-button"><i class="fa fa-bars fa-3x"></i></p>
							<h4 id="page-name" style="padding:9px;">Takings</h4>
						</section>
						<section class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
							<a href="index.php"><button class="btn btn-default btn-lg pull-right" style="margin:10px">Back to Register</button></a>
						</section>
					</section>
				</section>
			</section>
			<div class="tab hidden" id="pane-primary-home" data-page-name="Home">
				<?php require("views/home.php"); ?>
			</div>
			<div class="tab hidden" id="pane-primary-overview" data-page-name="Overview">
				<?php require("views/overview.php"); ?>
			</div>
			<div class="tab" id="pane-primary-takings" data-page-name="Takings">
				<?php require("views/takings.php"); ?>
			</div>
			<div class="tab hidden" id="pane-primary-transactions" data-page-name="Transactions">
				<?php require("views/transactions.php"); ?>
			</div>
			<div class="tab hidden" id="pane-primary-operators" data-page-name="Operators">
				<?php require("views/operators.php"); ?>
			</div>
			<div class="tab hidden" id="pane-primary-suppliers" data-page-name="Suppliers">
				<?php require("views/suppliers.php"); ?>
			</div>
			<div class="tab hidden" id="pane-primary-inventory" data-page-name="Inventory">
				<?php require("views/inventory.php"); ?>
			</div>
			<div class="tab hidden" id="pane-primary-departments" data-page-name="Departments">
				<?php require("views/departments.php"); ?>
			</div>
		</section>
        </section>
    </section>
	<?php require_once("modals/transactionProducts.php"); ?>
	<?php require_once("modals/productModal.php"); ?>
</body>
</html>
