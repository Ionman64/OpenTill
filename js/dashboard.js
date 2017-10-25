function el(tagName, options) {
	if (tagName.length == 0) {
		return;
	}
	var options = options || {};
	var el = document.createElement(tagName);
	if (options.id) {
		el.id = options.id;
		delete options.id;
	}
	if (options.class) {
		el.className = options.class;
		delete options.class;
	}
	if (options.html) {
		el.innerHTML = options.html;
		delete options.html;
	}
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}
function logout() {
	$.ajax({
		url:"api/kvs.php?function=LOGOUT",
		dataType: "JSON",
		success: function(data) {
			if (data.success) {
				window.location = "login.php";
			}
		}
	});
}
$(document).ready(function() {
	$.ajaxSetup({
		method:"POST",
		dataType:"JSON"
	});
	$("#logout").click(function() {
		logout();
	});
	window.takings = new Takings();
	window.takings.init();
	window.transactions = new Transactions();
	window.transactions.init();
	window.operators = new Operators();
	window.operators.init();
	window.suppliers = new Suppliers();
	window.suppliers.init();
	window.inventory = new Inventory();
	window.inventory.init();
	window.departments = new Departments();
	window.departments.init();
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
	setInterval(function() {
		$("#timeline").empty();
		var currentPercentage = percentage((moment().hour()*60)+moment().minute(), 24*60);
		var div = el("div", {class:"progress-bar progress-bar-success", style:"width:" + currentPercentage + "%"});
		var span = el("span", {class:"sr-only"});
		div.appendChild(span);
		$("#timeline").append(div);
		var div = el("div", {class:"progress-bar progress-bar-default", style:"width:" + (100-currentPercentage) + "%"});
		var span = el("span", {class:"sr-only"});
		div.appendChild(span);
		$("#timeline").append(div);
	}, 1000);
});
function percentage(value, total) {
	return Math.floor(value/total*100);
}
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