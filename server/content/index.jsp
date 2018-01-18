<!--
 _____                    _____ _ _ _ 
|  _  |                  |_   _(_) | |
| | | |_ __   ___ _ __     | |  _| | |
| | | | '_ \ / _ \ '_ \    | | | | | |
\ \_/ / |_) |  __/ | | |   | | | | | |
 \___/| .__/ \___|_| |_|   \_/ |_|_|_| v0.3
      | |                             
      |_|                             
-->
<!DOCTYPE html>
<html>
<head>
	<title>OpenTill</title>
	<link rel="shortcut icon" href="img/site/icon.ico">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self' http://localhost:8888/drawer http://127.0.0.1:8888/drawer http://localhost:8888/receipt http://127.0.0.1:8888/receipt; style-src 'self' 'unsafe-inline'">
	<meta name="robots" content="noindex, nofollow">
	<meta name="viewport" content="width=device-width, user-scalable=no">
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
	<section class="offline-banner hidden">
		<p>Offline mode-Transactions are being saved locally and will be uploaded when connection is restored</p>
	</section>
	<section id="newMessages" class="notify custom-btn-black hidden">
		<label>You have a new message</label><br>
		<i>Click to Reply</i>
	</section>
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
	<section class="menu overlay overlay-ontop hidden" id="menu">
		<section class="menu-buttons">
			<a href="login.jsp">
				<section class="menu-button btn-info text-center">
					<i class="fa fa-dashboard"></i>
					<section class="footer">
						<span>Dashboard</span>
					</section>
				</section>
			</a>
			<section class="menu-button btn-success text-center" id="chat-button">
				<i class="fa fa-comments"></i>
				<section class="footer">
					<span>Chat</span>
				</section>
			</section>
			<section class="menu-button btn-warning text-center" id="signout">
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
					<button class="btn btn-danger no-digit" data-function="clear-button"><span class="hidden-xs hidden-sm hidden-md">Clear</span><span class="hidden-lg">AC</span></button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-default no-digit" data-function="no-sale"><span class="hidden-xs hidden-sm hidden-md">No Sale</span><span class="hidden-lg">N/S</span></button>
				</section>
				<section class="col-md-4 col-xs-4 col-sm-4 col-lg-4">
					<button class="btn btn-info no-digit" data-function="pay-out"><span class="hidden-xs hidden-sm hidden-md">Pay Out</span><span class="hidden-lg">P/O</span></button>
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
					<button class="btn btn-default no-digit" id="refund" disabled="disabled" data-function="refund"><span class="hidden-xs hidden-sm hidden-md">Refund</span><span class="hidden-lg">RF</span></button>
				</section>
			</section>
		</section>
		<section id="department"></section>
		</section>
	<section class="col-md-9 col-xs-9 col-sm-9 col-lg-9">
		<section id="tableColumns" class="row shadow-bottom">
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
				<h1>No items, scan an item to begin</h1>
			</section>
		</section>
		<section class="row hidden" id="table-holder">
			<section id="table" class="col-md-12 col-xs-12 col-sm-12 col-lg-12">
				
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
				<section class="col-md-2">
					<p class="navbar-text" id="menu-button"><i class="fa fa-bars fa-2x"></i></p>
					<p class="navbar-text hidden" id="non-barcode-btn"><i class="fa fa-search fa-2x"></i></p>
				</section>
				<section class="col-md-2">
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
	
	<jsp:include page="modals/productMenuModal.php"></jsp:include>
	<jsp:include page="modals/cashOut.php"></jsp:include>
	<jsp:include page="modals/caseModal.php"></jsp:include>
	<jsp:include page="modals/chat.php"></jsp:include>
	<jsp:include page="modals/departmentInfo.php"></jsp:include>
	<jsp:include page="modals/newProduct.php"></jsp:include>
	<jsp:include page="modals/operatorInfo.php"></jsp:include>
	<jsp:include page="modals/orderModal.php"></jsp:include>
	<jsp:include page="modals/priceOverrideModal.php"></jsp:include>
	<jsp:include page="modals/productModal.php"></jsp:include>
	<jsp:include page="modals/supplierInfo.php"></jsp:include>
	<jsp:include page="modals/supplierModal.php"></jsp:include>
	<jsp:include page="modals/takings.jsp"></jsp:include>
	<jsp:include page="modals/transactionProducts.php"></jsp:include>
</body>
</html>
