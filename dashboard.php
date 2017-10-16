<?php
	session_start();
	if (!isset($_SESSION['user'])) {
		header('Location: login.php');
	}
?>
<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />

	<!--bootstrap-->
	<!--<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/js/bootstrap.min.js"></script>-->
<!--Datatables-->					
	<!--Datatables-->					
	<link rel="stylesheet" type="text/css" href="thirdParty/DataTables/custom-build/custom.css"/>
	<script type="text/javascript" src="thirdParty/DataTables/custom-build/custom.js"></script>
	<style>
		body {
			 overflow:hidden;
		}
		.dataTables_filter {
			float:right;
		}
		#viewport {
			padding:5px;
			margin-top:5px;
		}
		.sidebar {
			background: #444 none repeat scroll 0 0;
			height:1000px;
		}
		.sidebar>ul {
			width:100%;
			list-style:none;
			padding:0;
		}
		.sidebar>ul li {
			padding:10px;
			text-indent:15px;
			min-height:50px;
			border-top:1px solid #bbb;
		}
		.sidebar>ul li:last-child {
			border-bottom:1px solid #bbb;
		}
		.sidebar>ul li:hover {
			cursor:pointer;
			background:#999;
		}
		.sidebar>ul li.active:hover {
			cursor:disabled;
			background:#aaa;
		}
		.sidebar>ul li.active {
			cursor:not-allowed;
			background:#999;
		}
		.sidebar>ul li>a {
			text-decoration: none;
			color:#fff;
			font-size:24px;
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
		.custom-navbar {
			width:100%;
			background:#444;
			color:#fff;
			margin:0;
			left:0;
			top:0;
			min-height:65px;
			padding-left:10px;
			padding-right:10px;
		}
		.custom-btn {
			bottom: 0px;
			width: 250px;
			height: 50px;
			color: #fff;
			position: absolute;
			background: #aa0000;
			border: 0;
			font-weight:bold;
		}
		.no-padding {
			padding:0;
		}
		.card {
			width:100%;
			min-height:50px;
			padding:15px;
			text-align:left;
			border:1px solid #000;
			border-radius:5px;
			color:#aaa;
		}
		.card .title {
			font-size:18px;
			padding:0;
			margin:0;
		}
		.card .info {
			color:#000;
			font-size:38px;
			padding:0;
			margin:0;
			margin-top:5px;
		}
		.card .change {
			font-size:26px;
			
		}
		.card .sub-title {
			font-size:14px;
			font-style:italic;
			padding:0;
			margin:0;
			margin-top:5px;
		}
		.card .fa:hover {
			font-color:#000;
			cursor:pointer;
		}
		.card-success {
			background:#2b654c;
			color:#fff;
			border:0;
		}
	</style>
	<title>KVS-Dashboard</title>
	<script type="text/javascript" src="js/transactions.js"></script>
</head>
<body>
	<section class="container-fluid no-padding">
		<section class="row">
			<section class="col-md-2 no-padding">
				<section class="row">
					<section class="col-md-12">
						<section class="custom-navbar" style="min-height:65px;">
							<section class="container-fluid">
								<section class="row">
									<section class="col-md-12">
										<h3>OpenTill</h3>
									</section>
								</section>
							</section>
						</section>
					</section>
				</section>
				<section class="row">
					<section class="col-md-12">
						<section class="sidebar">
						<ul class="sidebar-nav">
							<li class="active">
								<a href="#" data-page="takings">Overview</a>
							</li>
							<li>
								<a href="#" data-page="takings">Takings</a>
							</li>
							<li>
								<a href="#" data-page="dashboard">Transactions</a>
							</li>
							<li>
								<a href="#" data-page="labels">Labels</a>
							</li>
							<li>
								<a href="#" data-page="operators">Operators</a>
							</li>
							<li>
								<a href="#" data-page="orders">Orders</a>
							</li>
							<li>
								<a href="#" data-page="suppliers">Suppliers</a>
							</li>
						   <li>
								<a href="#" data-page="suppliers">Departments</a>
							</li>
							<li>
								<a href="#" data-page="index">Back To Register</a>
							</li>
							<li>
								<a href="#" data-page="index">Documentation</a>
							</li>
						</ul>
						</section>
					</section>
				</section>
        </section>
		<section class="col-md-10 no-padding">
			<section class="custom-navbar">
				<section class="container-fluid">
					<section class="row">
						<section class="col-md-2 col-md-offset-10">
							<button class="btn btn-default btn-lg pull-right" id="logout" style="margin:10px">Logout</button>
						</section>
					</section>
				</section>
			</section>
			<section class="container-fluid" style="padding-top:10px;">
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
				<section class="col-sm-2 col-md-2">
					<section class="card">
						<h3 class="title">
							Sales Today <i class="fa fa-ellipsis-v pull-right"></i>
						</h3>
						<h3 class="info">
							<span class="currency">£</span>400<span class="change">.00</span>
						</h3>
						<h3 class="sub-title">
							Last Week: £300.00 <span class="text-success">(+25%)</span>
						</h3>
					</section>
					
					<section id="viewport" class="panel panel-default hidden">
					
					</section>
				</section>
			</section>
		</section>
        </section>
        <!-- /#page-content-wrapper -->
    </section>
	</section>
	<script>
		$("#menu-toggle").click(function(e) {
			e.preventDefault();
			$("#wrapper").toggleClass("toggled");
		});
    </script>
	<?php require_once("modals/transactionProducts.php"); ?>
</body>
</html>
