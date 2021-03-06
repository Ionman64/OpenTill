<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8"/>
	<link rel="shortcut icon" href="img/site/icon.ico">
	<!-- <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'"/> -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta name="viewport" content="width=device-width, initial-scale=1"/>
	<meta name="robots" content="noindex, nofollow"/>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<link rel="stylesheet" href="thirdParty/bootstrap4/css/bootstrap.min.css"/>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<!--accountingJS-->
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--ChartJS-->
	<script type="text/javascript" src="thirdParty/chartJS/js/chart.bundle.js"></script>
	<!--bootstrap-->
	<link rel="stylesheet" href="thirdParty/animate/css/animate.css"/>
	<script src="thirdParty/bootstrap4/js/bootstrap.min.js"></script>
	<!--touchspin-->
	<script src="thirdParty/touchspin/js/jquery.bootstrap-touchspin.min.js"></script>
	<link rel="stylesheet" href="thirdParty/touchspin/css/jquery.bootstrap-touchspin.min.css" />
	<!-- Bootstrap Toggle -->
	<link href="thirdParty/bootstrap-toggle/css/bootstrap-toggle.min.css" rel="stylesheet">
	<script src="thirdParty/bootstrap-toggle/js/bootstrap-toggle.min.js"></script>
	<!-- CalendarView -->
	<link href="thirdParty/CalendarView/css/calendar.css" rel="stylesheet">
	<script src="thirdParty/CalendarView/js/calendar.js"></script>
	<!--Bootbox-->
	<script src="thirdParty/bootbox/js/bootbox.min.js"></script>
	<!--Mithril-->
	<script src="thirdParty/mithril/js/mithril.min.js"></script>
	<script src="thirdParty/requirejs/require.js"></script>
	<script src="js/dashboard.js"></script>
	<link href="css/index.css" rel="stylesheet" type="text/css"/>
	<link href="css/dashboard.css" rel="stylesheet" type="text/css"/>
	
	<style>
	.col-md-* {
		margin:0;
		padding:0;
	}
	</style>
	<title>KVS-Dashboard</title>
</head>
<body class="no-select">
	<section id="loading-overlay" class="bg-dark">
		<section id="loader"></section>
	</section>
	<section id="mithril-navbar-container">
	
	</section>
	<section id="mithril-notification-container">
		
	</section>
	<section class="container-fluid no-padding" id="mithril-container">
		<!-- <section class="row">
		<article class="col-lg-10 col-md-9 col-sm-9 col-xs-8 no-padding-left">
		<section class="row" style="padding-top:10px">
			<section class="col-md-6">
				
			</section>
			<section class="col-md-6">
				<form role="form" onsubmit="return false;" class="form-inline pull-right">
				    <input type="text" class="form-control input-lg" id="overview-select-day" title="Select Date"></input>
				    <div class="form-group">
				        <button class="btn btn-default btn-lg" id="overview-prev-day" title="Previous Day">Prev Day</button>
						<button class="btn btn-default btn-lg" id="overview-next-day" title="Next Day" disabled="disabled">Next Day</button>
				     </div>
				</form>
				<section class=" inline-block">
					
					
					<!--  <input type="checkbox" id="autoupdateoverview"/>
				</section>
				
			</section>
		</section>
			<div class="tab hidden" id="pane-primary-overview" data-page-name="Overview">
				<section class="import-modal" page="./views/overview.jsp"></section>
			</div>
			<div class="tab" id="pane-primary-takings" data-page-name="Takings">
				<section class="import-modal" page="./views/takings.jsp"></section>
			</div>
			<div class="tab hidden" id="pane-primary-transactions" data-page-name="Transactions">
				<section class="import-modal" page="./views/transactions.jsp"></section>
			</div>
			<div class="tab hidden" id="pane-primary-operators" data-page-name="Operators">
				<section class="import-modal" page="./views/operators.jsp"></section>
			</div>
			<div class="tab hidden" id="pane-primary-suppliers" data-page-name="Suppliers">
				<section class="import-modal" page="./views/suppliers.jsp"></section>
			</div>
			<div class="tab hidden" id="pane-primary-inventory" data-page-name="Inventory">
				<section class="import-modal" page="./views/inventory.jsp"></section>
			</div>
			<div class="tab hidden" id="pane-primary-products" data-page-name="Departments">
				<section class="import-modal" page="./views/products.jsp"></section>
			</div>
			<div class="tab hidden" id="pane-primary-departments" data-page-name="Products">
				<section class="import-modal" page="./views/departments.jsp"></section>
			</div>
			<div class="tab hidden" id="pane-primary-orders" data-page-name="Orders">
				<section class="import-modal" page="./views/orders.jsp"></section>
			</div>
		</article>
    </section>
    </section>
    <section class="import-modal" page="modals/transactionProducts.jsp"></section>
    <section class="import-modal" page="modals/productModal.jsp"></section>
    <section class="import-modal" page="modals/supplierModal.jsp"></section>
    <section class="import-modal" page="modals/transactionModal.jsp"></section>
    <section class="import-modal" page="modals/departmentModal.jsp"></section>
    <section class="import-modal" page="modals/createDepartment.jsp"></section>
    <section class="import-modal" page="modals/exportTakings.jsp"></section>
    <section class="import-modal" page="modals/operatorInfo.jsp"></section>
    <section class="import-modal" page="modals/createOperator.jsp"></section>
    <section class="import-modal"  page="modals/supplierInfo.jsp"></section>
	<section class="import-modal"  page="modals/createSupplier.jsp"></section>-->
	</section>
</body>
</html>
