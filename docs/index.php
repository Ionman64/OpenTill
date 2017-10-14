<!DOCTYPE html>
<html>
<head>
	<title>KVS-Docs</title>
	<link REL="SHORTCUT ICON" href="../img/site_icon.png"/>
	<!--jQuery-->
	<script type="text/javascript" src="https://www.goldstandardresearch.co.uk/thirdParty/jQuery/js/jquery.min.js"></script>
	<!--bootstrap-->
	<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/bootstrap/css/bootstrap.min.css"/>
	<!--font awesome-->
	<link rel="stylesheet" href="https://www.goldstandardresearch.co.uk/thirdParty/font-awesome/css/font-awesome.min.css" />
	<style>
		.nav .nav {
			padding-left:20px;
		}
		.nav .fa {
			padding-left:1em;
		}
	</style>
	<script>
		$(document).ready(function() {
			$("#nav").on("click", "a", function() {
				if ($(this).hasClass("expanded")) {
					$(this).children("i").removeClass("fa-caret-down");
					$(this).children("i").addClass("fa-caret-right");
					$(this).removeClass("expanded");
				}
				else {
					$(this).children("i").addClass("fa-caret-down");
					$(this).children("i").removeClass("fa-caret-right");
					$(this).addClass("expanded");
				}
			});
		});
	</script>
</head>
<body>
	<section class="container-fluid">
		<section class="row">
			<section class="col-md-3">
				<ul class="nav">
					<li class="active"><a href="#" data-page="introduction">Introduction<i class="fa fa-caret-down"></i></a></li>
					<li>
						<a href="#gettingStarted">Getting Started<i class="fa fa-caret-down"></i></a>
						<ul class="nav">
							<li><a href="#">System Requirements</a></li>
							<li><a href="#">Supported Hardware</a></li>
						</ul>
					</li>
					<li>
						<a href="#products">Products<i class="fa fa-caret-down"></i></a>
						<ul class="nav">
							<li><a href="#">"You have added a new product"</a></li>
							<li><a href="#">Updating a product</a></li>
							<li><a href="#">Removing a product</a></li>
						</ul>
					</li>
					<li>
						<a href="#products">Departments<i class="fa fa-caret-down"></i></a>
						<ul class="nav">
							<li><a href="#">Adding a Departments</a></li>
							<li><a href="#">Updating a Department</a></li>
							<li><a href="#">Removing a Department</a></li>
						</ul>
					</li>
					<li>
						<a href="#labels">Labels<i class="fa fa-caret-down"></i></a>
						<ul class="nav">
							<li><a href="#">Introduction to Labels</a></li>
							<li><a href="#">Adding a Product Label</a></li>
							<li><a href="#">Printing labels</a></li>
							<li><a href="#">Clearing Labels</a></li>
						</ul>
					</li>
					<li>
						<a href="#inventory">Inventory<i class="fa fa-caret-down"></i></a>
						<ul class="nav">
							<li><a href="#">Setting up Automatic Inventory</a></li>
							<li><a href="#">Seeing Current Inventory</a></li>
						</ul>
					</li>
					<li>
						<a href="#orders">Orders<i class="fa fa-caret-down"></i></a>
						<ul class="nav">
							<li><a href="#">Creating Orders</a></li>
							<li><a href="#">Creating automatic Orders</a></li>
							<li><a href="#">Finding Orders</a></li>
							<li><a href="#">Adjusting Orders</a></li>
							<li><a href="#">Completing Orders</a></li>
						</ul>
					</li>
					<li>
						<a href="#operators">Cashiers/Operators<i class="fa fa-caret-down"></i></a>
						<ul class="nav">
							<li><a href="#">Admin</a></li>
							<li><a href="#">Adding an Operator</a></li>
							<li><a href="#">Creating a Barcode Keycard</a></li>
							<li><a href="#">Disabling an Operator</a></li>
						</ul>
					</li>
				</ul>
			</section>
			<section class="col-md-9" data-page="introduction">
				<section>
					<h2 id="introduction">Introduction</h2>
					<h4>OpenTill is designed to automate the smallest store to the largest supermarket using cloud technology</h4>
					<p>OpenTill helps you with;</p>
					<ul>
						<li>Keeping track of products</li>
						<li>Labeling Products</li>
						<li>Handling Multiple Cashiers/Operators<li>
					</ul>
				</section>
				<section class="">
					<h2 id="gettingStarted">Getting Started</h2>
					<h4>This section is useful for those who are ready to install OpenTill and Start using the System, before you begin you should read this 
					section to ensure that you have everything you need ready to setup OpenTill.</h4>
					<h3>System Requirements</h3>
					<p>You will need the following to run OpenTill;
					<ul>
						<li><a href="http://lubuntu.net/">lubuntu 17.04*</a></li>
						<li><a href="https://www.mozilla.org/en-US/firefox/new/">Mozilla Firefox 43+</a></li>
						<li><a href="https://www.python.org/downloads/">Python 3.4+</a></li>
						<li><a href="https://github.com/python-escpos/python-escpos">escpos-python</a></li>
					</ul>
						<i>* OpenTill currently only supports lubuntu OS, more operating systems are coming soon</i>
					</p>
					<h3>Running OpenTill locally</h3>
					<p>To run OpenTill locally, you will need;
					<ul>
						<li><a href="http://lubuntu.net/">Apache or Nginx Server</a></li>
						<li><a href="https://www.mozilla.org/en-US/firefox/new/">PHP 7.0+</a></li>
						<li><a href="https://www.python.org/downloads/">MySQL</a></li>
					</ul>
					</p>
					<h3>Supported Hardware</h3>
					<p>OpenTill supports a limited range of POS hardware;
					<ul>
						<li><a href="#">Any USB barcode scanner that acts as a keyboard external device</a></li>
						<li><a href="#">Safescan Cash Drawer</a></li>
					</ul>
					</p>
				</section>
				<section class="">
					<h2 id="products">Products</h2>
					<h4>OpenTill </h4>
				</section>
				<section class="">
					<h2 id="departments">Departments</h2>
					<h4>OpenTill </h4>
				</section>
				<section class="">
					<h2 id="labels">Labels</h2>
					<h4>OpenTill </h4>
				</section>
				<section class="">
					<h2 id="orders">Orders</h2>
					<h4>OpenTill </h4>
				</section>
				<section class="">
					<h2 id="operators">Cashiers/Operators</h2>
					<h4>An Operator is anyone that uses the OpenTill</h4>
					<h3>Admin</h3>
				</section>
			</section>
		</section>
	</section>
</body>
</html>
