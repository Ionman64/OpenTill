<!DOCTYPE html>
<html>
<head>
	<title>OpenTill</title>
	<meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self' http://localhost:8888/drawer http://127.0.0.1:8888/drawer http://localhost:8888/receipt http://127.0.0.1:8888/receipt; style-src 'self' 'unsafe-inline'">
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
	<!--ChartJS-->
	<script type="text/javascript" src="thirdParty/chartJS/js/chart.bundle.js"></script>
	<!--custom script-->
	<script type="application/javascript" src="js/product_class.js"></script>
	<script type="application/javascript" src="js/transaction_class.js"></script>
	<script type="application/javascript" src="js/cash_register.js"></script>
	<link rel="stylesheet" href="css/index.css"/>
</head>
<body class="no-select">
	<section class="container-fluid">
	<section class="overlay hidden" id="item-search">
		<section class="container-fluid">
		<section class="row">
			<section class="col-md-2 col-md-offset-10" style="padding-top:10px;">
				<button class="btn btn-danger" style="float:right" id="cancelSearch">Cancel Search</button>
			</section>
			<section class="col-md-12" style="padding-top:10px">
				<ul id="item-search-list" class="navigation-list">
					
				</ul>
			</section>
		</section>
		</section>
	</section>
	<section class="overlay overlay-ontop hidden" id="menu">
		<section class="container-fluid">
			<section class="row">
				<section class="col-md-2 col-md-offset-10" style="padding-top:10px;">
					<button class="btn btn-danger" style="float:right" id="cancelMenu">Close Menu</button>
				</section>
				<section class="col-md-12" style="padding-top:10px">
					<ul id="menu-items" class="navigation-list text-center">
						<li><a href="takings.php"><i class="fa fa-line-chart fa-5x"></i><br>Takings</a></li>
						<li><a href="labels.php"><i class="fa fa-tags fa-5x"></i><br>Labels</a></li>
						<li><a href="dashboard.php"><i class="fa fa-tasks fa-5x"></i><br>Transactions</a></li>
						<li><a href="suppliers.php"><i class="fa fa-truck fa-5x"></i><br>Suppliers</a></li>
						<li><a href="operators.php"><i class="fa fa-users fa-5x"></i><br>Operators</a></li>
						<li><a href="departments.php"><i class="fa fa-table fa-5x"></i><br>Departments</a></li>
					</ul>
				</section>
			</section>
		</section>
	</section>
	<section class="row">
		<section class="col-md-3 col-xs-3 col-sm-3 col-lg-3" style="padding-top:10px">
			<form id="barcodeform" action="#">
			<section class="input-group">
				<input type="text" id="barcode" class="form-control input-lg" placeholder="Barcode/Search"/>
				<section class="input-group-btn">
					<input type="submit" value="Enter" id="go" class="btn btn-default btn-lg"/>
				</section>
			</section>
			</form>
			<section id="keypad" class="keypad" data-focus="#barcode">
			<section class="row" style="padding-top:10px">
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-danger no-digit" data-function="clear-button">Clear</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default no-digit" data-function="no-sale">N/S</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-info no-digit" data-function="pay-out">P/O</button>
				</section>
			</section>
			<section class="row" style="padding-top:10px">
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">7</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">8</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">9</button>
				</section>
			</section>
			<section class="row" style="padding-top:10px">
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">4</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">5</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">6</button>
				</section>
			</section>
			<section class="row" style="padding-top:10px">
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">1</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">2</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">3</button>
				</section>
			</section>
			<section class="row" style="padding-top:10px">
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">0</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default">00</button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default no-digit" id="refund" disabled="disabled" data-function="refund">REFUND</button>
				</section>
			</section>
		</section>
		<section id="department">
			
			
		</section>
		</section>
	<section class="col-md-9 col-xs-9 col-sm-9 col-lg-9">
		<section class="row" id="item-footer" style="padding-top:10px;border-bottom:1px solid #aaa;">
			<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4 col-md-offset-1 col-xs-offset-1 col-sm-offset-1 col-lg-offset-1">
				<label>Items <span id="total-items"></span></label>
			</section>
			<section class="col-md-1 col-xs-1 col-sm-1 col-lg-1">
				<label>Price</label>
			</section>
			<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
				<label>Quantity <span id="total-quantity"></span></label>
			</section>
			<section class="col-md-2 col-xs-2 col-sm-2 col-lg-2">
				<label>Total <span id="total-cost"></span></label>
			</section>
		</section>
		<section class="row" id="no-goods">
			<section class="col-md-12 col-xs-12 col-sm-12 col-lg-12">
				<h2>No Items, scan an item to begin</h2>
			</section>
		</section>
		<section class="row hidden" id="goods-holder">
			<section id="goods-table" class="col-md-12 col-xs-12 col-sm-12 col-lg-12">
				
			</section>
		</section>
	</section>
	</section>
	</section>
	<section id="notify" class="notify-fixed-bottom hidden">
		<section class="inner">
			<p><b class="notify-message">Logged in as:</b></p>
			<p class="dismiss pull-right italic">Click to dismiss</p>
		</section>
	 </section>
	<section class="navbar navbar-inverse navbar-fixed-bottom">
		<div class="container-fluid">
			<section class="row">
				<!--<section class="col-md-6">
					<button class="navbar-btn btn btn-lg btn-default pull-left"><i class="fa fa-sign-out"></i></button>
					<h4 class="navbar-text">Signed in as Danielle</h4>
				</section>-->
				<section class="col-md-1">
					<p class="navbar-text" id="menu-button"><i class="fa fa-bars fa-2x"></i></p>
				</section>
				<section class="col-md-3">
					<h2 class="navbar-text navbar-left" id="operator-name"></h2>
				</section>
				<section class="col-md-4">
					<h2 class="navbar-text navbar-right" id="totalCost">Total : £0.00</h2>
				</section>
				<section class="col-md-4">
					<div class="btn-group navbar-right" role="toolbar" aria-label="...">
						<button class="navbar-btn btn btn-lg btn-default" id="clearTrans" disabled>Clear</button>
						<button class="navbar-btn btn btn-lg btn-default" id="printReceipt">Print Receipt</button>
						<button class="navbar-btn btn btn-lg btn-success" id="cashOutMainButton">CASH OUT</button>
					</div>
				</section>
			</section>
		</div>
	</section>
	<?php require_once("modals/productModal.php"); ?>
	<?php require_once("modals/cashOut.php"); ?>
	<?php require_once("modals/productMenuModal.php"); ?>
	<?php require_once("modals/priceOverrideModal.php"); ?>
	<?php require_once("modals/newProduct.php"); ?>
	<?php require_once("modals/supplierModal.php"); ?>
</body>
</html>
