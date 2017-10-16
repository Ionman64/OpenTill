<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<script src="thirdParty/popper/popper.min.js"></script>
	<link rel="stylesheet" type="text/css" href="thirdParty/simple-sidebar/simple-sidebar.css"/>
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
		.custom-navbar {
			width:100%;
			background:#444;
			color:#fff;
			margin:0;
			left:0;
			top:0;
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
	</style>
	<title>KVS-Dashboard</title>
	<script type="text/javascript" src="js/transactions.js"></script>
</head>
<body>
	<section id="wrapper" class="toggled">
        <!-- Sidebar -->
        <section id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li class="sidebar-brand">
                    <a href="#">
                        OpenTill
                    </a>
                </li>
			    <li>
                    <a href="#" data-page="takings"><i class="fa fa-line-chart" aria-hidden="true"></i>&nbsp;Takings</a>
                </li>
                <li>
                    <a href="#" data-page="dashboard"><i class="fa fa-tasks" aria-hidden="true"></i>&nbsp;Transactions</a>
                </li>
                <li>
                    <a href="#" data-page="labels"><i class="fa fa-tags" aria-hidden="true"></i>&nbsp;Labels</a>
                </li>
                <li>
                    <a href="#" data-page="operators"><i class="fa fa-users" aria-hidden="true"></i>&nbsp;Operators</a>
                </li>
                <li>
                    <a href="#" data-page="orders"><i class="fa fa-truck" aria-hidden="true"></i>&nbsp;Orders</a>
                </li>
                <li>
                    <a href="#" data-page="suppliers"><i class="fa fa-truck" aria-hidden="true"></i>&nbsp;Suppliers</a>
                </li>
			   <li>
                    <a href="#" data-page="suppliers"><i class="fa fa-table" aria-hidden="true"></i>&nbsp;Departments</a>
                </li>
            </ul>
			<a href="index.php"><button class="custom-btn custom-btn-red fixed-bottom">Back to Register</button></a>
        </section>
        <!-- /#sidebar-wrapper -->

        <!-- Page Content -->
        <section id="page-content-wrapper">
			<section class="custom-navbar">
			<section class="container-fluid">
				<section class="row">
					<section class="col-md-2">
						<a href="#menu-toggle" class="navbar-text" id="menu-toggle"><i class="fa fa-bars fa-2x"></i></a>
					</section>
					<section class="col-md-2 col-md-offset-8">
						<button class="btn btn-danger btn-lg pull-right" id="logout" style="margin:10px">Logout</button>
					</section>
				</section>
			</section>
		</section>
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
				<section class="col-sm-12 col-md-12">
					<section id="viewport" class="panel panel-default">
					
					</section>
				</section>
			</section>
		</section>
        </section>
        <!-- /#page-content-wrapper -->
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
