<!DOCTYPE html>
<html>
	<head>
		<title>OpenTill</title>
		<!--<meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self' http://localhost:8888/drawer http://127.0.0.1:8888/drawer http://localhost:8888/receipt http://127.0.0.1:8888/receipt; style-src 'self' 'unsafe-inline'">-->
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
		<link rel="stylesheet" href="css/index.css"/>
		<style>
			.panel {
				text-align:center;
			}
			body {
				overflow-y:scroll;
			}
			#search-for-product {
				z-index:9999;
			}	
			.overlay {
				position: absolute;
				width: 100%; 
				height:100%;
				top:0; 
				left:0;
				background:rgba(0,0,0,0.9);
				opacity: 1;
				transition: all 1s;
				-webkit-transition: all 1s;
				max-height:100%;
				max-width:100%;
				z-index:999;
			}
		</style>
		<script>
			function getParam(name) {
			  	var params = {};
				if (location.search) {
					var parts = location.search.substring(1).split('&');
					for (var i = 0; i < parts.length; i++) {
						var nv = parts[i].split('=');
						if (!nv[0]) continue;
						params[nv[0]] = nv[1] || true;
					}
				}
				return (params[name] ? params[name] : null);	
			}
			var delay = (function(){
			  var timer = 0;
			  return function(callback, ms){
			  clearTimeout (timer);
			  timer = setTimeout(callback, ms);
			 };
			})();
			function showSearches(searchString) {
				if ((searchString.indexOf("[") > -1) || (searchString.indexOf("]") > -1) || (searchString.length == 0)) {
					var holder = $("#item-search-list")[0];
					$(holder).empty();
					$("#item-search").addClass("hidden");
					return;
				}
				$.ajax({
					url: "api/kvs.jsp?function=SEARCH",
					data : {"search" : searchString},
					success : function(data) {
						var holder = $("#item-search-list")[0];
						$(holder).empty();
						$("#item-search").removeClass("hidden");
						$.each(data, function(key, item) {
							if ($("#search-for-product").val().length == 0) {
								$("#item-search").addClass("hidden");
								return;
							}
							var li = document.createElement("li");
							li.className = "btn btn-default";
							$(li).attr("product-data", item.id);
							var span1 = document.createElement("span");
							span1.innerHTML = item.name + "<br>@" + formatMoney(item.price, "£");
							li.appendChild(span1);
							holder.appendChild(li);	
						});
					}	
				});
			}
			function formatMoney(amount, prefix) {
				var prefix = prefix || "";
				return accounting.formatMoney(amount, prefix);
			}
			$(document).ready(function() {
				$.ajaxSetup({
					dataType:"JSON",
					method:"POST"
				});
				$("#search-for-product").keyup(function() {
					var text = $(this).val();
					if (text.length == 0) {
						$("#item-search").addClass("hidden");
						return;
					}
					if (/^\d+$/.test(text) == false) {
						delay(function() {showSearches(text)}, 200);
					}
				});
				$("#item-search-list").on("click", "li", function() {
					/*if (!getOperator()) {
						bootbox.alert("Please scan your operator id to continue");
						return;
					}
					if (!getTransaction()) {
						createTransaction()
					}*/
					location.href = "https://www.goldstandardresearch.co.uk/kvs/product.jsp?id=" + $(this).attr("product-data");
					$("#item-search").addClass("hidden");
					$("#search-for-product").val("");
				});
				$.ajax({
					url:"api/kvs.jsp?function=GETPRODUCT",
					data:{"id":getParam("id")},
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
						getSalesData(14, getParam("id"));
					}	
				});
			});
			function getSalesData(daysToLookBack, id) {
				$.ajax({
					url:"api/kvs.jsp?function=GETPRODUCTSALES",
					data:{"start":(moment(moment().subtract(daysToLookBack-1, "days").format("YYYY-MM-DD")).format("x")/1000), "end":(moment(moment().add(1, "days").format("YYYY-MM-DD")).format("x")/1000)-1, "id":id},
					success:function(data) {
						if (!data.success) {
							alert("There has been an error");
							return;
						}
						var date = moment();
						date.subtract(daysToLookBack, "days");
						var SevenDaysAgoTimestamp = moment(moment().format("YYYY-MM-DD")).subtract(7, "days").format("x");
						var salesData = {};
						var productSales = 0;
						for (var i=0;i!==daysToLookBack;i++) {
							salesData[date.add(1, "days").format("dd Do MMM")] = 0;
						}
						$.each(data.sales, function(key, item) {
							salesData[moment(item.created*1000).format("dd Do MMM")]++;
							if (moment(item.created*1000).format("x") > SevenDaysAgoTimestamp) {
								productSales++;	
							}
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
				        backgroundColor: "#3333ff",
				        borderColor: "#3333ff",
				        borderWidth: 1,
				        data: data
				    }]
		    	};
				var ctx = document.getElementById("canvas").getContext("2d");
				window.myBar = new Chart(ctx, {
					type: 'bar',
					data: barChartData,
					options: {
						responsive: true,
						legend: {
							position: 'top',
						},
						title: {
							display: true,
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
	</head>
	<body>
		<section class="navbar navbar-inverse navbar-fixed-top">
			<div class="container-fluid">
				<section class="row">
					<section class="col-md-2 col-lg-2 col-sm-2 col-xs-2">
						<a href="index.jsp"><button class="navbar-btn btn btn-lg btn-default">Back to till</button></a>
					</section>
					<section class="col-md-3 col-lg-3 col-sm-3 col-xs-3 col-md-offset-7 col-lg-offset-7 col-sm-offset-7 col-xs-offset-7">
						<form class="navbar-form navbar-right">
						 <div class="form-group">
							<input type="text" class="form-control input-lg " id="search-for-product" placeholder="Search for Product"/>
						  </div>
						 </form>
					</section>
				</section>
			</div>
		</section>
		<section class="overlay hidden" id="item-search">
			<section class="container-fluid" style="padding-top:10px;">
			<section class="row">
				<section class="col-md-2 col-md-offset-10" style="padding-top:10px;">
					<button class="btn btn-danger" style="float:right" id="cancelSearch">Cancel Search</button>
				</section>
				<section class="col-md-12" style="padding-top:10px">
					<ul id="item-search-list" class="navigation-list"></ul>
				</section>
			</section>
			</section>
		</section>
		<section class="container-fluid" style="padding-top:60px;">
			<section class="row" style="padding-top:5px;">
				<section class="col-md-12">
					<h1 class="text-center" id="product-name">Unknown Product</h1>
				</section>
				<section class="col-md-2">
					<section class="row">
						<section class="col-md-12">
							<div class="panel panel-default">
							  <div class="panel-body">
								<h6>Sold This Week</h6>
								<h4 id="product-sales">0</h4>
							  </div>
							</div>
						</section>
						<section class="col-md-12">
							<div class="panel panel-default">
							  <div class="panel-body">
								<h6>Product Last Updated</h6>
								<h4 id="product-updated">0</h4>
							  </div>
							</div>
						</section>
					</section>
				</section>
				<section class="col-md-8">
					<div class="panel panel-default">
					  	<div class="panel-body">
							<canvas id="canvas"></canvas>
						</div>
					</div>
				</section>
			</section>
		</section>
	</body>
</html>
