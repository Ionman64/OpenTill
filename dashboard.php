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
	<script>
		$(document).ready(function() {
			$.ajaxSetup({
				method:"POST",
				dataType:"JSON"
			});
			$.ajax({
				url:"api/kvs.php?function=GETPRODUCT",
				data:{"id":"EBCDB3C0-E71F-4EFC-AB5F-EDBE1119E687"},
				success:function(data) {
					if (!data.success) {
						alert("Could not find product");
						return;
					}
					$("#product-name").html(data.product.name);	
					if (data.product.updated == 0) {
						$("#product-updated").html("Never");
					}
					else {
						$("#product-updated").html(moment(data.product.updated*1000).format("YYYY-MM-DD"));
					}
					getSalesData(0, "EBCDB3C0-E71F-4EFC-AB5F-EDBE1119E687");
				}	
			});
		});
		function getSalesData(daysToLookBack, id) {
				$.ajax({
					url:"api/kvs.php?function=GETPRODUCTSALES",
					data:{"start":(moment(moment().subtract(daysToLookBack-1, "days").format("YYYY-MM-DD")).format("x")/1000), "end":(moment(moment().add(1, "days").format("YYYY-MM-DD")).format("x")/1000)-1, "id":id},
					success:function(data) {
						if (!data.success) {
							alert("There has been an error");
							return;
						}
						//var date = moment();
						var date = moment(moment().format("YYYY-MM-DD"));
						date.subtract(1, "hours");
						var salesData = {};
						var productSales = 0;
						for (var i=0;i<24;i++) {
							productSales = productSales + 10;
							salesData[date.add(1, "hours").format("HH:mm")] = productSales;
						}
						$.each(data.sales, function(key, item) {
							productSales = productSales + 10;
							salesData[moment(item.created*1000).format("HH:mm")] = productSales;
						});
						$("#product-sales").html(productSales);
						renderGraph(salesData);
					}	
				});
			}
			function renderGraph(salesData) {
				data = [];
				labels = [];
				$.each(salesData, function(key, value) {
					labels.push(key);
					data.push(value);
				});
		    	var barChartData = {
				    labels: labels,
				    datasets: [{
				        label: 'Units Sold',
				        borderColor: "#3333ff",
				        borderWidth: 1,
				        data: data
				    }]
		    	};
				var ctx = document.getElementById("canvas").getContext("2d");
				window.myBar = new Chart(ctx, {
					type: 'line',
					data: barChartData,
					options: {
						responsive: true,
						legend: {
							position: 'bottom',
						},
						title: {
							display: false,
							text: 'Sales'
						},
						scales: {
							yAxes: [{
								ticks: {
								    beginAtZero:true
								}
							}]
						}
					}
				});
			}
	</script>
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
			background: #5A7A6A none repeat scroll 0 0;
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
		.padding-right {
			padding-right:40px;
		}
		.card {
			text-align:left;
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
			background:#4F8970;
			color:#fff;
			border:0;
		}
		canvas {
			margin-top:1em;
		}
		.mui-tabs__bar > li > a {
			color:#fff;
			cursor:pointer;
		}
	</style>
	<title>KVS-Dashboard</title>
	<script type="text/javascript" src="js/transactions.js"></script>
</head>
<body>
	<section class="container-fluid">
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
					<section class="col-md-12 hidden-sm hidden-xs">
						<section class="sidebar">
						<ul class="sidebar-nav">
							<li class="active">
								<a href="#" data-page="takings">Overview</a>
							</li>
							<li style="border-left:5px solid #000">
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
			<section class="container-fluid padding-right" style="overflow-y:auto;">
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
				<section class="col-sm-6 col-md-2 col-xs-12">
					<section class="mui-panel card">
						<h3 class="title">
							Sales Today 
							<div class="mui-dropdown pull-right">
							  <i class="fa fa-ellipsis-v" class="mui-btn mui-btn--primary" data-mui-toggle="dropdown"></i>
							  <ul class="mui-dropdown__menu" style="left:-30px;">
								<li><a href="#">See More</a></li>
							  </ul>
							</div>
						</h3>
						<h3 class="info">
							<span class="currency">£</span>400<span class="change">.00</span>
						</h3>
						<h3 class="sub-title">
							Last Week: £300.00 <span class="text-success">(+25%)</span>
						</h3>
					</section>
				</section>
				<section class="col-md-12">
					<section class="mui-panel" style="padding-top:0;">
						<section class="row" style="background:#000;padding-left:5px">
							<section class="col-md-1 col-sm-6 col-xs-6">
								<div class="mui--text-display1 text-center" style="padding:5px;color:#f4f4f4;">Takings</div>
							</section>
							<section class="col-md-11 col-sm-6 col-xs-6">
								<ul class="mui-tabs__bar pull-right">
								  <li class="mui--is-active"><a data-mui-toggle="tab" data-mui-controls="pane-default-1">Today</a></li>
								  <li><a data-mui-toggle="tab" data-mui-controls="pane-default-2">Week</a></li>
								  <li><a data-mui-toggle="tab" data-mui-controls="pane-default-3">Month</a></li>
								   <li><a data-mui-toggle="tab" data-mui-controls="pane-default-4">Year</a></li>
								</ul>
								<div class="mui-tabs__pane mui--is-active hidden" id="pane-default-1">Pane-1</div>
								<div class="mui-tabs__pane hidden" id="pane-default-2">Pane-2</div>
								<div class="mui-tabs__pane hidden" id="pane-default-3">Pane-3</div>
								<div class="mui-tabs__pane hidden" id="pane-default-4">Pane-4</div>
							</section>
							
						</section>
						<section class="row">
							<section class="col-md-8">
								<canvas id="canvas" style="height:300px;"></canvas>
							</section>
							<section class="col-md-4">
								<table class="table table-stripped">
								  <thead>
									  <tr>
										<th>Department</th>
										<th>Total</th>
									  </tr>
								  </thead>
								  <tbody>
								  <tr>
									<td>Alfreds Futterkiste</td>
									<td>Maria Anders</td>
								  </tr>
								  <tr>
									<td>Centro comercial Moctezuma</td>
									<td>Francisco Chang</td>
								  </tr>
								  <tr>
									<td>Ernst Handel</td>
									<td>Roland Mendel</td>
								  </tr>
								  <tr>
									<td>Island Trading</td>
									<td>Helen Bennett</td>
								  </tr>
								  <tr>
									<td>Laughing Bacchus Winecellars</td>
									<td>Yoshi Tannamuri</td>
								  </tr>
								  <tr>
									<td>Magazzini Alimentari Riuniti</td>
									<td>Giovanni Rovelli</td>
								  </tr>
								  </tbody>
								</table>
							</section>
						</section>
					</section>
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
