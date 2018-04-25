<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="img/site/icon.ico">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<!-- <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self' http://localhost:8888/drawer http://127.0.0.1:8888/drawer http://localhost:8888/receipt http://127.0.0.1:8888/receipt; style-src 'self' 'unsafe-inline'"/>-->
	<meta name="robots" content="noindex, nofollow"/>
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
<!--Datatables-->					
	<link rel="stylesheet" type="text/css" href="thirdParty/DataTables/custom-build/custom.css"/>
	<script type="text/javascript" src="thirdParty/DataTables/custom-build/custom.js"></script>
	<script>
	    var JSON = {
			"AA1":["A", "B"],
			"AA2":["C"],
			"AA3":["D"],
			"AA4":["E"]
		};
		var items = {
			"A":{
				"current_stock":5,
				"barcode":"101010",
				"name":"Walkers Crisps"
			},
			"B":{
				"current_stock":20
			},
			"C":{
				"current_stock":10
			},
			"D":{
				"current_stock":15
			},
			"E":{
				"current_stock":40
			}
		};
		function renderTable() {
			var holder = $("#table")[0];
			$(holder).empty();
			$.each(JSON, function(key, row) {
				var tr = document.createElement("tr");
				var td = document.createElement("td");
				td.innerHTML = key;
				tr.appendChild(td);
				var td = document.createElement("td");
				$.each(row, function(key, value) {
					var item = items[value];
					var section = document.createElement("section");
					section.innerHTML = item.current_stock;
					section.setAttribute("data-id", value);
					section.className = "box";
					td.appendChild(section);
				});
				tr.appendChild(td);
				holder.appendChild(tr);
			});
		}
		$(document).ready(function() {
			renderTable();
			$("#table").on("click", ".box", function() {
				var item = items[$(this).attr("data-id")];
				var code = "";
				var p = document.createElement("p");
				$.each(item, function(key, value) {
					p.appendChild(document.createTextNode(key + ":" + value));
					p.appendChild(document.createElement("br"));
				});
				
				$("#details").empty().append(p);
			});
		});
	</script>
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
		.box {
		  border:5px solid #999999;
		  width:100px;
		  height:100px;
		  background:grey;
		  text-align:center;
		  padding:20px;
		  margin:5px;
		  cursor:pointer;
		  display:inline-block;
		  font-size:40px;
		}
		.box:hover {
		  border:5px solid #1030ee;
		  width:100px;
		  height:100px;
		  margin:5px;
		}
		tr>td:first-child {
		  text-align:center;
		  font-weight:bold;
		}
	</style>
	<title>KVS-Refill</title>
	<script type="text/javascript" src="js/order.js"></script>
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
				<table class="table" id="table" border="1">
					
				</table>
			</section>
		</section>
		<section class="row">
			<section class="col-md-12">
				<section class="panel panel-danger">
					<section class="panel-body" id="details">
						No Box Selected
					</section>
				</section>
			</section>
		</section>
	</section>
</body>
</html>
